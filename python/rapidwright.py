# import rapidwright
import jpype
import jpype.imports
from jpype.types import *


jpype.startJVM(classpath=[
    "/home/bcheng/workspace/tools/RapidWright/bin",
    "/home/bcheng/workspace/tools/RapidWright/jars/*"
])

from com.xilinx.rapidwright.device import Device
device = Device.getDevice("xc7z020clg400-1")
print("Device Name: ", device.getName())

jpype.shutdownJVM()
