
`timescale 1ps/1ps

module tb_deserializer;

    localparam LENGTH = 24;
    reg tb_clk;
    reg tb_rst;
    reg tb_en;
    reg tb_din;
    reg tb_din_valid;
    reg tb_ready;
    wire dut_ready;
    wire [LENGTH-1:0] tb_dout;
    wire tb_dout_valid;

    reg [LENGTH-1:0] tb_word;
    int num_errors = 0;
    reg tb_err;

    // instantiation unit under test 
    deserializer_fsm
    #(
        .LENGTH(LENGTH)
    ) dut (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
        .i_en(tb_en),
        .i_din(tb_din),
        .i_din_valid(tb_din_valid),
        .i_ready(tb_ready),
        .o_ready(dut_ready),
        .ov_dout(tb_dout),
        .o_dout_valid(tb_dout_valid)
    );


    task assert_and_report(input [LENGTH-1:0] expected, input [LENGTH-1:0] actual);
    begin
        if (actual == expected) begin
            $display("SUCCESS!\n Expected: %b\n Actual:   %b", expected, actual);
            tb_err = 1'b0;
        end else begin
            $display("FAILURE!\n Expected: %b\n Actual:   %b", expected, actual);
            num_errors = num_errors + 1;
            tb_err = 1'bx;
        end
    end
    endtask // assert_and_report


    task test_word(input [LENGTH-1:0] word);
    begin
        tb_din_valid = 0;
        @(posedge tb_clk);
        tb_en = 1;
        tb_rst = 0;
        tb_din_valid = 1;

        wait (dut_ready == 1'b1);
        @(posedge tb_clk);

        // serialize word, LSB first in
        for (int i = 0; i < LENGTH; i++) begin
            tb_din = word[i];
            @(posedge tb_clk);
        end

        tb_din_valid = 0;
        wait(tb_dout_valid == 1'b1); // waits for dout valid from dut
        tb_ready = 1; // consume dout from dut

        assert_and_report(word, tb_dout);

        @(posedge tb_clk);
        tb_ready = 0;

        // arbitrary wait
        for (int i = 0; i < 50; i++) begin
            @(posedge tb_clk);
        end

    end
    endtask // test_word


    always #5000 tb_clk = ~tb_clk;
    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;

        num_errors = 0;
        tb_clk = 1;
        tb_rst = 1;
        tb_en = 0;

        tb_word = 24'b1111_1111_0000_0000_1111_1111;
        $display("Word: %h = %b", tb_word, tb_word);
        test_word(tb_word);
        $display();

        tb_word = 24'b0000_0000_1111_1111_0000_0000;
        $display("Word: %h = %b", tb_word, tb_word);
        test_word(tb_word);
        $display();

        tb_word = 24'b1010_1111_0101_1110_1011_1001;
        $display("Word: %h = %b", tb_word, tb_word);
        test_word(tb_word);
        $display();


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


    initial begin
        #50ms; // Wait for 1ms simulation time
        $display("Simulation terminated after 50 milliseconds.");
        $finish;
    end // initial
endmodule
