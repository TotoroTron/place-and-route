
module tb_shift_reg;

    localparam LENGTH = 8;
    int num_errors = 0;
    reg tb_clk;
    reg tb_rst;
    reg tb_din;
    reg [LENGTH-1:0] tb_word;
    wire tb_dout;

    // instantiation unit under test 
    shift_reg #(.LENGTH(LENGTH)) dut (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
        .i_din(tb_din),
        .o_dout(tb_dout)
    );

    always #5 tb_clk = ~tb_clk;

    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;

        num_errors = 0;
        tb_clk = 1;
        tb_rst = 1;
        tb_word = $urandom();
        #10;

        tb_rst = 0;
        for (int i = 0; i < LENGTH; i++) begin
            tb_din = tb_word[i];
            #10;
        end

        $finish;
    end // initial
endmodule
