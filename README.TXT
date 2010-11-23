RUNNING
=======

Run the main method from the sim.agent.Main.

If the java version used is not JDK 6 then add the following argument to VM : -Dcom.sun.management.jmxremote
This is used for allowing jconsole to connect to the application on a local machine.

If a remote connection is needed through jconsole then the following arguments mus tbe added to VM :
-Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

The main class can be invoked with 3 parameters : period, timeUnit and initialDelay
- period : the time period between each metrics read; 5 is the default value
- timeUnit : the time unit of the period and initial delay parameters. Possible values 
are MS, S (default value) or M (milliseconds, seconds or minutes)
- initialDelay : the time to delay first execution; 0 is default

If no parameter is supplied then default ones are used.

The agent will run 2 thread :
- one thread will run periodically and will read and display into console the system metrics
- the other thread will start up a an Http Server on http://localhost:8088/agent This server 
will be listening for incoming sim.data.MethodMetrics objects

Each time an MethodMetrics it's received it will be displayed in the console using it's toString() method.

NOTE : If you get an NPE upon running running please do a Clean of all projects (it is some problem with 
M2Eclipse maven, the resources are not copied in the target folder)
