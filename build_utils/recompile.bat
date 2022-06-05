@echo on
title Recompiler batch script for schematics-in-worlds
echo This script allow to recompile the mod to use the generated resources
echo launching minecraft a first time after adding a new schem file
echo Let's recompile the mod!
cd /D "%~dp0"
mkdir schematicsinworld-1.16.5-1.0
cd schematicsinworld-1.16.5-1.0
jar xf ../schematicsinworld-1.16.5-1.0.jar
rmdir /q /s data
cd ../
move resources/data schematicsinworld-1.16.5-1.0
cd schematicsinworld-1.16.5-1.0
jar cMf ../schematicsinworld-1.16.5-1.0.jar *
cd ../
rmdir /q /s schematicsinworld-1.16.5-1.0
rmdir /q /s resources