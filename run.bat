@echo off
set source="%~1.tri"
set destination="%~1.tam"
set compiler_setting="%~2"
java -cp build/libs/Triangle-Tools.jar triangle.Compiler programs/%source% -o=%destination% %compiler_setting%
java -cp build/libs/Triangle-Tools.jar triangle.abstractMachine.Interpreter %destination% 