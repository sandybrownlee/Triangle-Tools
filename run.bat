set source="%~1.tri"
set destination="%~1.tam"
set compiler_setting1="%~2"
set compiler_setting2="%~3"
set compiler_setting3="%~4"
set compiler_setting4="%~5"
java -cp build/libs/Triangle-Tools.jar triangle.Compiler -s programs/%source% -o %destination% %compiler_setting1% %compiler_setting2% %compiler_setting3% %compiler_setting4% %compiler_setting5%
java -cp build/libs/Triangle-Tools.jar triangle.abstractMachine.Interpreter %destination%