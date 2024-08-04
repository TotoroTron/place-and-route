import rapidwright

from com.xilinx.rapidwright.device import Device

device = Device.getDevice("xc7z020-1clg400c")
print("Device Name: ", device.getName())
