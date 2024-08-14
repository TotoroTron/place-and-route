# import rapidwright
import jpype
import jpype.imports
from jpype.types import JBoolean 


jpype.startJVM(classpath=[
    "/home/bcheng/workspace/tools/RapidWright/bin",
    "/home/bcheng/workspace/tools/RapidWright/jars/*"
])

from com.xilinx.rapidwright.device import * 
from com.xilinx.rapidwright.design import * 
from com.xilinx.rapidwright.placer.blockplacer import BlockPlacer

device = Device.getDevice("xc7z020clg400-1")
print("Device Name: ", device.getName())

project_dir = "/home/bcheng/workspace/dev/place-and-route/"
synthesized_dcp = f"{project_dir}/tcl/synthesized.dcp"
placed_dcp = f"{project_dir}/tcl/placed.dcp"

design = Design.readCheckpoint(synthesized_dcp)

debug = jpype.JClass("boolean")(True)
# cannot pass booleans into java. why?

design = BlockPlacer.placeDesign(design, debug) 
design.writeCheckpoint(placed_dcp)

jpype.shutdownJVM()

