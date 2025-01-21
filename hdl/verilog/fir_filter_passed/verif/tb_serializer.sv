
`timescale 1ps/1ps

module tb_serializer;

    localparam LENGTH = 24;
    reg tb_clk;
    reg tb_rst;
    reg tb_en;
    reg [LENGTH-1:0] tb_din;
    reg tb_din_valid;
    reg tb_ready;
    wire dut_ready;
    wire tb_dout;
    wire tb_dout_valid;

    reg [LENGTH-1:0] tb_word;
    logic [LENGTH-1:0] serial_word;
    int num_errors = 0;
    logic tb_err;

    // instantiation unit under test 
    serializer_fsm
    #(
        .LENGTH(LENGTH)
    ) dut (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
        .i_en(tb_en),
        .iv_din(tb_din),
        .i_din_valid(tb_din_valid),
        .i_ready(tb_ready),
        .o_ready(dut_ready),
        .o_dout(tb_dout),
        .o_dout_valid(tb_dout_valid)
    );


    task assert_and_report(input [LENGTH-1:0] expected, input [LENGTH-1:0] actual);
    begin
        if (actual == expected) begin
            $display(" SUCCESS!\n  Expected: %b\n  Actual:   %b", expected, actual);
            tb_err = 1'b0;
        end else begin
            $display(" FAILURE!\n  Expected: %b\n  Actual:   %b", expected, actual);
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
        tb_din = word;
        tb_din_valid = 1;

        // wait for dut to be ready to accept data
        wait (dut_ready == 1'b1);
        @(posedge tb_clk);


        tb_din_valid = 0;
        wait(tb_dout_valid == 1'b1); // waits for dout valid from dut
        @(posedge tb_clk);
        tb_ready = 1; // consume dout from dut

        // transmit data from tb to dut
        for (int i = 0; i < LENGTH; i++) begin
            serial_word[i] = tb_dout;
            @(posedge tb_clk);
        end

        @(posedge tb_clk);
        tb_ready = 0;

        assert_and_report(word, serial_word);

        // arbitrary wait
        for (int i = 0; i < 50; i++) begin
            @(posedge tb_clk);
        end

    end
    endtask


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
