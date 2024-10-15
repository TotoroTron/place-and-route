`timescale 1ns/1ps
module tb_counter;

    reg tb_clk;
    reg tb_rst;
    wire [3:0] tb_led;

    // instantiation unit under test 
    top_level dut (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
        .ov_led(tb_led)
    );

    always #5 tb_clk = ~tb_clk;

    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;
        tb_clk = 0;
        tb_rst = 1;

        #25;

        tb_rst = 0;
        #100000;
        tb_rst = 1;
        #100000;
        tb_rst = 0;
        #100000;
        $finish;
    end // initial

endmodule
