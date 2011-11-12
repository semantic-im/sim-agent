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

SYSTEM METRICS
==============
1) System Load Average (double - %)
  Returns the system load average for the last minute. The system load average is the sum of the number of runnable 
entities queued to the available processors and the number of runnable entities running on the available processors 
averaged over a period of time. The way in which the load average is calculated is operating system specific but is
typically a damped time-dependent average.
  If the load average is not available, a negative value is returned.
  This metric is designed to provide a hint about the system load and may be queried frequently. The load average may
be unavailable on some platform where it is expensive to implement this method.

2) Total System Free Memory (long - Kb)
Get the Actual total free system memory.

3) Total System Used Memory (long - Kb)
Get the Actual total used system memory (e.g. Linux minus buffers).

4) Total System Used Swap Space (long - Kb)
Get the Total used system swap.

5) System Open File Descriptor Count (long - count of file descriptors)
Get the Total number of open file descriptors for all the system processes.
Returns 0 if access to system processes or file descriptors is not allowed due to permission rights.

6) Swap In (long - number of swap pages in)
Get the Swap Pages in. Amount of memory swapped in from disk.

7) Swap Out (long - number of swap pages out)
Get the Swap Pages out. Amount of memory swapped to disk.

8) I/O in (long - Kb)
Get the Number of physical disk bytes read for all the file systems in the system.

9) I/O out (long - Kb)
Get the Number of physical disk bytes written for all the file systems in the system.

10) CPU User Percentage (double - %)
CPU percentage usage for time spent running non-kernel code. (user time, including nice time)

11) CPU System Percentage (double - %)
CPU percentage usage for time spent running kernel code. (system time)

12) CPU Idle Percentage (double - %)
CPU percentage usage for time spent idle. Prior to Linux 2.5.41, this includes IO-wait time.

13) CPU Wait Percentage (double - %)
CPU percentage usage for time spent waiting for IO. Prior to Linux 2.5.41, included in idle.

14) CPU Irq Percentage (double - %)
CPU percentage usage for time spent servicing interrupts.

15) CPU User Time (double - seconds)
Get the Total system cpu user time.
CPU time usage for the time the CPU has spent running users' processes that are not niced.

16) CPU System Time (double - seconds)
Get the Total system cpu kernel time.
CPU time usage for the time the CPU has spent running the kernel and its processes.

17) CPU Idle Time (double - seconds)
Get the Total system cpu idle time.

18) CPU Wait Time (double - seconds)
Get the Total system cpu io wait time.

19) CPU Irq Time (double - seconds)
Get the Total system cpu time servicing interrupts.
