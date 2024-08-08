
# import rapidwright
import jpype
import jpype.imports
from jpype.types import *


jpype.startJVM(classpath=[
    "/home/bcheng/workspace/tools/RapidWright/bin",
    "/home/bcheng/workspace/tools/RapidWright/jars/*"
])

from com.xilinx.rapidwright.device import * 
from com.xilinx.rapidwright.design import * 
from com.xilinx.rapidwright.site import *

device = Device.getDevice("xc7z020clg400-1")
print("Device Name: ", device.getName())





jpype.shutdownJVM()
