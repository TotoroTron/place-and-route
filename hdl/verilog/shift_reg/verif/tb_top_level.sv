`timescale 1ns/1ns

module tb_top_level;

    localparam LENGTH = 32;
    int num_errors = 0;
    reg tb_clk;
    reg tb_rst;
    reg tb_din;
    reg [LENGTH-1:0] tb_word;
    wire tb_dout;

    // instantiation unit under test 
    top_level #(.LENGTH(LENGTH)) dut (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
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

    task test_word(input [LENGTH-1:0] word);
    begin
        tb_rst = 0;
        // serialize word, LSB first in
        for (int i = 0; i < LENGTH; i++) begin
            tb_din = word[i];
            @(posedge tb_clk);
        end
        // read output
        for (int i = 0; i < LENGTH; i++) begin
            @(posedge tb_clk);
            assert_and_report(word[i], tb_dout);
        end
        tb_rst = 1; // reset the dut
        @(posedge tb_clk);
        tb_rst = 0;
    end
    endtask // test_word

    always #50 tb_clk = ~tb_clk;

    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;

        num_errors = 0;
        tb_clk = 1;
        tb_rst = 1;

        repeat (100) begin
            tb_word = $urandom();
            $display("Word: %h = %b", tb_word, tb_word);
            test_word(tb_word);
            $display();
        end
        
        $display();
        $display("Total number of errors: %d", num_errors);
        $display();

        $finish;
    end // initial
endmodule
