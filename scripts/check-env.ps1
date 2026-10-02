# =============================================================================
#  SmartTask - Verification de l'environnement de developpement
#
#  Usage :  .\scripts\check-env.ps1
#
#  Si PowerShell refuse d'executer le script :
#      Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
#
#  Note : ce fichier est volontairement en ASCII pur (sans accents) pour
#  s'afficher correctement dans toutes les consoles Windows.
# =============================================================================

$ErrorActionPreference = 'SilentlyContinue'

$script:Ok      = 0
$script:Failed  = 0
$script:Warned  = 0
$script:Actions = @()

function Write-Head($text) {
    Write-Host ""
    Write-Host "=== $text " -ForegroundColor Cyan -NoNewline
    Write-Host ("=" * [Math]::Max(0, 60 - $text.Length)) -ForegroundColor Cyan
}

function Pass($label, $detail) {
    Write-Host "  [OK]   " -ForegroundColor Green -NoNewline
    Write-Host ("{0,-22} {1}" -f $label, $detail)
    $script:Ok++
}

function Fail($label, $detail, $action) {
    Write-Host "  [KO]   " -ForegroundColor Red -NoNewline
    Write-Host ("{0,-22} {1}" -f $label, $detail)
    $script:Failed++
    if ($action) { $script:Actions += $action }
}

function Warn($label, $detail, $action) {
    Write-Host "  [!]    " -ForegroundColor Yellow -NoNewline
    Write-Host ("{0,-22} {1}" -f $label, $detail)
    $script:Warned++
    if ($action) { $script:Actions += $action }
}

# -----------------------------------------------------------------------------
Write-Host ""
Write-Host "  SmartTask - controle de l'environnement" -ForegroundColor White
Write-Host "  $(Get-Date -Format 'yyyy-MM-dd HH:mm')" -ForegroundColor DarkGray

# --- 1. Backend --------------------------------------------------------------
Write-Head "Backend (Java / Spring Boot)"

$java = Get-Command java -ErrorAction SilentlyContinue
if ($java) {
    $raw = (& java -version 2>&1) -join ' '
    $m = [regex]::Match($raw, '"(\d+)\.')
    if (-not $m.Success) { $m = [regex]::Match($raw, 'version "?(\d+)') }
    $major = 0
    if ($m.Success) { $major = [int]$m.Groups[1].Value }

    if ($major -ge 17) {
        Pass "Java (JRE)" "version $major -- requis : 17+"
    } else {
        Fail "Java (JRE)" "version $major -- trop ancienne" "Installer un JDK 17+ : winget install EclipseAdoptium.Temurin.17.JDK"
    }
} else {
    Fail "Java (JRE)" "introuvable" "Installer un JDK 17+ : winget install EclipseAdoptium.Temurin.17.JDK"
}

$javac = Get-Command javac -ErrorAction SilentlyContinue
if ($javac) {
    Pass "Java (compilateur)" (((& javac -version 2>&1) -join ' ').Trim())
} else {
    Fail "Java (compilateur)" "javac introuvable -- tu as un JRE, pas un JDK" "Installer un JDK complet (pas seulement un JRE)"
}

if ($env:JAVA_HOME) {
    if (Test-Path (Join-Path $env:JAVA_HOME 'bin\javac.exe')) {
        Pass "JAVA_HOME" $env:JAVA_HOME
    } else {
        Fail "JAVA_HOME" "defini mais invalide : $env:JAVA_HOME" '[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "User")   puis rouvrir le terminal'
    }
} else {
    Fail "JAVA_HOME" "non defini" '[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "User")   puis rouvrir le terminal'
}

$root = Split-Path -Parent $PSScriptRoot
if (Test-Path (Join-Path $root 'backend\mvnw.cmd')) {
    Pass "Maven Wrapper" "backend\mvnw.cmd present"
} else {
    $mvn = Get-Command mvn -ErrorAction SilentlyContinue
    if ($mvn) {
        Warn "Maven Wrapper" "absent, mais Maven global est installe" "Normal avant l'etape 2 : le wrapper sera genere avec le projet backend"
    } else {
        Warn "Maven Wrapper" "absent (normal avant l'etape 2)" "Rien a faire : mvnw sera genere par Spring Initializr a l'etape 2"
    }
}

# --- 2. Frontend -------------------------------------------------------------
Write-Head "Frontend (Node / Angular)"

$node = Get-Command node -ErrorAction SilentlyContinue
if ($node) {
    $nv = (& node -v).TrimStart('v')
    $nmajor = [int]($nv.Split('.')[0])
    # Angular 20+ : Node ^20.19 || ^22.12 || >=24
    if ($nmajor -ge 20 -and ($nmajor % 2 -eq 0)) {
        Pass "Node.js" "v$nv -- version LTS, compatible Angular"
    } elseif ($nmajor -ge 20) {
        Warn "Node.js" "v$nv -- version impaire (non LTS)" "Angular recommande une version LTS paire (20, 22, 24)"
    } else {
        Fail "Node.js" "v$nv -- trop ancienne" "Installer Node LTS : winget install OpenJS.NodeJS.LTS"
    }
} else {
    Fail "Node.js" "introuvable" "Installer Node LTS : winget install OpenJS.NodeJS.LTS"
}

$npm = Get-Command npm -ErrorAction SilentlyContinue
if ($npm) { Pass "npm" "v$(& npm -v)" }
else      { Fail "npm" "introuvable" "npm est fourni avec Node.js -- reinstaller Node" }

$ng = Get-Command ng -ErrorAction SilentlyContinue
if ($ng) {
    $ngOut = (& ng version 2>&1) -join "`n"
    $ngm = [regex]::Match($ngOut, 'Angular CLI:\s*([\d\.]+)')
    if ($ngm.Success) { Pass "Angular CLI" "v$($ngm.Groups[1].Value)" }
    else              { Pass "Angular CLI" "installe" }
} else {
    Fail "Angular CLI" "introuvable" "npm install -g @angular/cli"
}

# --- 3. Base de donnees ------------------------------------------------------
Write-Head "Base de donnees et conteneurs"

$docker = Get-Command docker -ErrorAction SilentlyContinue
if ($docker) {
    $dv = (& docker --version 2>&1) -join ' '
    Pass "Docker (CLI)" $dv.Replace('Docker version ', 'v')

    $srv = (& docker info --format '{{.ServerVersion}}' 2>&1) -join ''
    if ($LASTEXITCODE -eq 0 -and $srv -and $srv -notmatch 'error|cannot') {
        Pass "Docker (demon)" "demarre -- serveur v$srv"

        $running = (& docker ps --filter "name=smarttask-postgres" --format "{{.Status}}" 2>&1) -join ''
        if ($running) {
            Pass "Conteneur PostgreSQL" $running
        } else {
            Warn "Conteneur PostgreSQL" "non demarre" "docker compose up -d   (depuis la racine du projet)"
        }
    } else {
        Fail "Docker (demon)" "non demarre" "Lancer Docker Desktop depuis le menu Demarrer"
    }
} else {
    Fail "Docker" "introuvable" "Installer Docker Desktop : winget install Docker.DockerDesktop"
}

$psql = Get-Command psql -ErrorAction SilentlyContinue
if ($psql) {
    Pass "Client psql" (((& psql --version 2>&1) -join ' ').Replace('psql (PostgreSQL) ', 'v'))
} else {
    Warn "Client psql" "introuvable" "Optionnel : pgAdmin (http://localhost:5050) suffit"
}

# --- 4. Outillage ------------------------------------------------------------
Write-Head "Outillage"

$git = Get-Command git -ErrorAction SilentlyContinue
if ($git) {
    Pass "Git" (((& git --version 2>&1) -join ' ').Replace('git version ', 'v'))
    Push-Location $root
    $inside = (& git rev-parse --is-inside-work-tree 2>&1) -join ''
    $top    = (& git rev-parse --show-toplevel 2>&1) -join ''
    Pop-Location
    if ($inside -eq 'true') {
        $normTop  = $top.Replace('/', '\').TrimEnd('\')
        $normRoot = $root.TrimEnd('\')
        if ($normTop -ieq $normRoot) {
            Pass "Depot git" "racine = le projet"
        } else {
            Warn "Depot git" "racine = $top" "Le projet n'a pas son propre depot -- executer : git init"
        }
    } else {
        Warn "Depot git" "pas encore initialise" "git init"
    }
} else {
    Fail "Git" "introuvable" "winget install Git.Git"
}

$code = Get-Command code -ErrorAction SilentlyContinue
if ($code) { Pass "VS Code" "installe" }
else       { Warn "VS Code" "commande 'code' introuvable" "Optionnel -- voir docs/00-INSTALLATIONS.md section 7" }

# --- 5. Ports ----------------------------------------------------------------
Write-Head "Ports du projet"

$ports = @(
    @{ P = 4200; N = 'Angular dev server';   Expect = 'free' },
    @{ P = 8080; N = 'API Spring Boot';      Expect = 'free' },
    @{ P = 5433; N = 'PostgreSQL SmartTask'; Expect = 'used' },
    @{ P = 5050; N = 'pgAdmin';              Expect = 'used' }
)

foreach ($e in $ports) {
    $conn = Get-NetTCPConnection -LocalPort $e.P -State Listen -ErrorAction SilentlyContinue
    if ($conn) {
        $pname = 'inconnu'
        $proc = Get-Process -Id ($conn | Select-Object -First 1).OwningProcess -ErrorAction SilentlyContinue
        if ($proc) { $pname = $proc.ProcessName }
        if ($e.Expect -eq 'used') {
            Pass "Port $($e.P)" "$($e.N) -- actif ($pname)"
        } else {
            Warn "Port $($e.P)" "$($e.N) -- deja occupe par '$pname'" "Liberer le port $($e.P) avant de lancer $($e.N)"
        }
    } else {
        if ($e.Expect -eq 'used') {
            Warn "Port $($e.P)" "$($e.N) -- inactif" "docker compose up -d"
        } else {
            Pass "Port $($e.P)" "$($e.N) -- libre"
        }
    }
}

# --- 6. Fichiers du projet ---------------------------------------------------
Write-Head "Fichiers du projet"

$expected = @(
    'README.md',
    '.gitignore',
    '.env.example',
    'docker-compose.yml',
    'docs\00-INSTALLATIONS.md',
    'docs\01-ARCHITECTURE.md',
    'docs\02-MODELE-DONNEES.md',
    'docs\03-API-REST.md',
    'docs\04-ROADMAP.md'
)
$missing = @()
foreach ($f in $expected) {
    if (-not (Test-Path (Join-Path $root $f))) { $missing += $f }
}
if ($missing.Count -eq 0) {
    Pass "Documentation" "$($expected.Count) fichiers presents"
} else {
    Fail "Documentation" "manquants : $($missing -join ', ')" "Fichiers de l'etape 1 absents ou deplaces"
}

if (Test-Path (Join-Path $root '.env')) {
    Pass ".env" "present (non versionne)"
} else {
    Warn ".env" "absent" "Copy-Item .env.example .env"
}

# --- Resume ------------------------------------------------------------------
Write-Head "Resume"
Write-Host ("  OK : {0}    Avertissements : {1}    Bloquants : {2}" -f $script:Ok, $script:Warned, $script:Failed)

if ($script:Actions.Count -gt 0) {
    Write-Host ""
    Write-Host "  A faire :" -ForegroundColor Yellow
    $i = 1
    foreach ($a in ($script:Actions | Select-Object -Unique)) {
        Write-Host ("   {0}. {1}" -f $i, $a) -ForegroundColor Yellow
        $i++
    }
}

Write-Host ""
if ($script:Failed -eq 0) {
    Write-Host "  Environnement pret. Tu peux passer a l'etape 2." -ForegroundColor Green
} else {
    Write-Host "  $($script:Failed) point(s) bloquant(s) a corriger avant l'etape 2." -ForegroundColor Red
    Write-Host "  Details : docs/00-INSTALLATIONS.md" -ForegroundColor DarkGray
}
Write-Host ""

exit $(if ($script:Failed -gt 0) { 1 } else { 0 })
