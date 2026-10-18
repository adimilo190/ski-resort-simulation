# build.ps1 - kompiluje caly projekt (lacznie z pakietem kadra.mapki w src/mapki) do out/.
# Foldery zrodel maja myslniki, a pakiety podkreslenia - javac ustala pakiet z deklaracji
# `package`, wiec kompilacja przez jawna liste wszystkich plikow .java dziala poprawnie.
# Cel: Java 21 (komputer students). Lokalnie moze byc nowszy JDK - uzywamy --release 21.
# Uwaga: skrypt celowo w ASCII (Windows PowerShell 5.1 czyta .ps1 jako ANSI, nie UTF-8 bez BOM).
#
# Uzycie:
#   .\build.ps1                  # kompiluje do out\
#   .\build.ps1 -Test            # kompiluje + uruchamia testy JUnit
#   .\build.ps1 -Run -MapDir "mapki-out" -Dane "tests\przykladowe-dane-1.txt"
#                                # kompiluje i uruchamia symulacje z asercjami (-ea)

param(
    [switch]$Run,
    [switch]$Test,
    [string]$MapDir = "mapki-out",
    [string]$Dane = "tests\przykladowe-dane-1.txt"
)

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot
$srcDir = Join-Path $root "src"
$outDir = Join-Path $root "out"
$jar = Join-Path $root "lib\junit-platform-console-standalone.jar"

# Czysty katalog wyjsciowy.
if (Test-Path $outDir) { Remove-Item -Recurse -Force $outDir }
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

# Zbierz wszystkie pliki .java ze src (pomijajac stary katalog src\out z czesci 1).
$pliki = Get-ChildItem -Path $srcDir -Recurse -Filter *.java |
    Where-Object { $_.FullName -notmatch '\\out\\' } |
    ForEach-Object { $_.FullName }

Write-Output "Kompiluje $($pliki.Count) plikow .java do out\ (Java 21, UTF-8)..."
& javac --release 21 -encoding UTF-8 -d $outDir $pliki
if ($LASTEXITCODE -ne 0) { throw "javac zakonczyl sie kodem $LASTEXITCODE" }
Write-Output "OK - kompilacja produkcji zakonczona sukcesem."

# Kompilacja i uruchomienie testow JUnit (standalone jar z lib\).
if ($Test) {
    $testy = Get-ChildItem -Path (Join-Path $root "test") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
    Write-Output "Kompiluje $($testy.Count) plikow testow..."
    & javac --release 21 -encoding UTF-8 -cp "$outDir;$jar" -d $outDir $testy
    if ($LASTEXITCODE -ne 0) { throw "javac (testy) zakonczyl sie kodem $LASTEXITCODE" }
    Write-Output "Uruchamiam testy JUnit..."
    & java -ea -jar $jar execute --class-path $outDir --scan-classpath --details=tree
}

# Uruchomienie symulacji. PowerShell nie obsluguje operatora `<` (i psuje kodowanie przy
# potokowaniu Get-Content), wiec uzywamy cmd /c z prawdziwym przekierowaniem `<` - jak na students.
if ($Run) {
    $daneSciezka = Join-Path $root $Dane
    Write-Output "Uruchamiam: java -ea -cp out Main `"$MapDir`" < $Dane"
    cmd /c "java -ea -cp `"$outDir`" Main `"$MapDir`" < `"$daneSciezka`""
}
