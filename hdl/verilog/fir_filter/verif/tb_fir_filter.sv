`timescale 1ns/1ns

module tb_shift_reg;

    localparam DATA_WIDTH = 24;
    localparam FIR_LENGTH = 128;
    int num_errors = 0;
    reg tb_clk;
    reg tb_rst;
    reg tb_en;
    reg tb_din;
    reg [DATA_WIDTH-1:0] tb_din;
    wire [DATA_WIDTH-1:0] tb_dout;
    wire tb_prod_overflow;
    wire tb_sum_overflow;

    // instantiation unit under test 
    top_level 
    #(
        .DATA_WIDTH(DATA_WIDTH),
        .FIR_LENGTH(FIR_LENGTH)
    ) dut (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
        .i_en(tb_en),
        .i_din(tb_din),
        .o_dout(tb_dout)
    );

    reg tb_err;
    task assert_and_report(input expected, input actual);
    begin
        if (actual == expected) begin
            $display("SUCCESS! Expected: %h, Actual: %h", expected, actual);
            tb_err = 1'b0;
        end else begin
            $display("FAILURE! Expected: %h, Actual: %h", expected, actual);
            num_errors = num_errors + 1;
            tb_err = 1'bx;
        end
    end
    endtask // assert_and_report

    task test_signal() begin
        
    end

    task push_word() begin
    end

    always #10 tb_clk = ~tb_clk;

    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;

        num_errors = 0;
        tb_clk = 1;
        tb_rst = 1;


        
        $display();
        $display("Total number of errors: %d", num_errors);
        $display();

        $finish;
    end // initial
endmodule
