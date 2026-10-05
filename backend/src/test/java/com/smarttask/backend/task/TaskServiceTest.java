package com.smarttask.backend.task;

import com.smarttask.backend.project.Project;
import com.smarttask.backend.project.ProjectMember;
import com.smarttask.backend.project.ProjectMemberRepository;
import com.smarttask.backend.project.ProjectRepository;
import com.smarttask.backend.project.ProjectRole;
import com.smarttask.backend.user.Role;
import com.smarttask.backend.user.User;
import com.smarttask.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test UNITAIRE : aucun contexte Spring ne demarre (rapide, quelques
 * millisecondes). Toutes les dependances de TaskService sont remplacees par
 * des "mocks" (de faux objets qu'on programme a repondre ce qu'on veut) --
 * on teste UNIQUEMENT la logique ecrite dans TaskService lui-meme.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private TaskService taskService;

    private User reporter;
    private User randomUser;
    private Task task;

    @BeforeEach
    void setUp() {
        // Construction manuelle (pas de @InjectMocks ici, plus explicite a lire)
        taskService = new TaskService(taskRepository, projectRepository, projectMemberRepository, userRepository, eventPublisher);

        reporter = new User();
        reporter.setId(UUID.randomUUID());
        reporter.setRole(Role.MEMBER);

        randomUser = new User();
        randomUser.setId(UUID.randomUUID());
        randomUser.setRole(Role.MEMBER);

        Project project = new Project();
        project.setId(UUID.randomUUID());

        task = new Task();
        task.setId(UUID.randomUUID());
        task.setProject(project);
        task.setReporter(reporter);
        task.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        // Remarque : le stub de taskRepository.save() N'EST PAS ici, mais dans
        // chaque test qui en a reellement besoin -- sinon Mockito (en mode strict)
        // signale une erreur "UnnecessaryStubbing" des qu'un test ne l'utilise pas
        // (ex: le test "forbidden" ci-dessous, qui echoue avant d'atteindre save()).
    }

    @Test
    void updateStatus_versDone_remplitCompletedAt() {
        // R10 : passer a DONE doit remplir completedAt
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = taskService.updateStatus(task.getId(), TaskStatus.DONE, reporter);

        assertThat(response.status()).isEqualTo(TaskStatus.DONE);
        assertThat(response.completedAt()).isNotNull();
    }

    @Test
    void updateStatus_sortantDeDone_videCompletedAt() {
        // R10 : ressortir de DONE doit remettre completedAt a null
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        task.setStatus(TaskStatus.DONE);
        task.setCompletedAt(Instant.now());

        var response = taskService.updateStatus(task.getId(), TaskStatus.IN_PROGRESS, reporter);

        assertThat(response.completedAt()).isNull();
    }

    @Test
    void updateStatus_parUnUtilisateurEtrangerAuProjet_leveForbidden() {
        // R11 : ni assignee, ni reporter, ni membre du projet -> refuse
        when(projectMemberRepository.findByProjectIdAndUserId(task.getProject().getId(), randomUser.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateStatus(task.getId(), TaskStatus.DONE, randomUser))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403");
    }

    @Test
    void updateStatus_parManagerDuProjet_estAutorise() {
        // R11 : un MANAGER du projet peut modifier, meme sans etre assignee/reporter
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ProjectMember membership = new ProjectMember();
        membership.setProjectRole(ProjectRole.MANAGER);
        when(projectMemberRepository.findByProjectIdAndUserId(task.getProject().getId(), randomUser.getId()))
                .thenReturn(Optional.of(membership));

        var response = taskService.updateStatus(task.getId(), TaskStatus.DONE, randomUser);

        assertThat(response.status()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void updateStatus_parAdminGlobal_estToujoursAutorise() {
        // Un ADMIN passe sans meme verifier l'appartenance au projet
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        randomUser.setRole(Role.ADMIN);

        var response = taskService.updateStatus(task.getId(), TaskStatus.DONE, randomUser);

        assertThat(response.status()).isEqualTo(TaskStatus.DONE);
    }
}
