#!/bin/bash
echo This script allow to recompile the mod to use the generated resources
echo launching minecraft a first time after adding a new schem file
echo Let\'s recompile the mod!
mkdir schematicsinworld-1.16.5-1.0
cd schematicsinworld-1.16.5-1.0
jar xf ../schematicsinworld-1.16.5-1.0.jar
rm -dr data
cd ../
mv resources/data schematicsinworld-1.16.5-1.0
cd schematicsinworld-1.16.5-1.0
jar cMf ../schematicsinworld-1.16.5-1.0.jar *
cd ../
rm -dr schematicsinworld-1.16.5-1.0
rm -dr resources
