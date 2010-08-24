#!/usr/bin/python

import sys,os,datetime,subprocess,time

################################################################################
# Configuration
################################################################################
LOCKFILE    = "/var/lock/oo-server"
APPNAME     = "OpenOffice"
PROCESSNAME = "soffice"
BINNAME     = "soffice.bin"
STARTCMD    = "soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard"

################################################################################
# Tools
################################################################################
def execute(cmd):
    # print cmd
    return os.system(cmd)

################################################################################
# Starts the server process.
################################################################################
def commandStart():
    print "Starting %s server..." % (APPNAME)
    
    cmd = STARTCMD

    # All the stdin, stdout, and stderr must be redirected
    # or otherwise Ant will hang when this start script returns.
    if execute(cmd + " </dev/null >/dev/null 2>&1 &"):
        print "Launching %s in server mode failed." % (APPNAME)
        sys.exit(1)

    # Wait a little to let it start.
    time.sleep(5)

    # It is more important to check the .bin name than the launch script
    pids = findProcessPIDs(BINNAME)
    if len(pids) > 0:
        print "Server started successfully with PID [%s]." % (pids[0])
    else:
        print "Starting server failed."
        sys.exit(2)

################################################################################
# Stop
################################################################################
def commandStop():
    pids = findProcessPIDs(PROCESSNAME)

    if len(pids) > 0:
        for pid in pids:
            stopProcess(pid)
    else:
        print "No running %s processes to kill." % (APPNAME)

    # allow next instance to run
    execute("rm -f %s" % (LOCKFILE))

def findProcessPIDs(name):
    pin = os.popen("ps -do pid,args | grep %s | grep -v grep | sed -e 's/^ \\+//' | cut -d ' ' -f 1" % (name), "r")
    pids = map(lambda x: x.strip(), pin.readlines())
    pin.close()

    return pids

def checkProcess(pid):
    pin = os.popen("ps -o pid -p %s --no-headers" % (pid), "r")
    pid = pin.readlines()
    pin.close()

    return len(pid) > 0

def stopProcess(pid):
    print "Killing existing %s process softly, PID [%s]" % (APPNAME, pid)
    execute("kill -8 " + pid)
    time.sleep(2);

    if (checkProcess(pid)):
        print "Killing existing %s process, PID [%s]" % (APPNAME, pid)
        execute("kill -1 " + pid)
        time.sleep(2);

        if (checkProcess(pid)):
            print "Power killing existing %s process, PID [%s]" % (APPNAME, pid)
            execute("kill -9 " + pid)
            time.sleep(2);

################################################################################
# Document Conversion
################################################################################
def convert(src, trg):
    print "Converting %s to %s..." % (src, trg)
    cmd = "java -jar lib/jodconverter/jodconverter-cli-2.2.2.jar %s %s" % (src, trg)
    if execute(cmd):
        print "Conversion command '%s' failed." % (cmd)
        sys.exit(1)
    else:
        print "Conversion succeeded."
    
################################################################################
# Testing
################################################################################

command = sys.argv[1]

if command == "stop":
    commandStop()

elif command == "start" or command == "restart":
    commandStart()

elif command == "convert":
    if len(sys.argv) < 4:
        print "Not enough parameters for conversion."
        sys.exit(1)

    if len(findProcessPIDs(BINNAME)) == 0:
        commandStart()

    srcfile = sys.argv[2]
    trgfile = sys.argv[3]
    convert(srcfile, trgfile)
else:
    print "Invalid command '%s'" % (command)
    sys.exit(1)

print "Done."
