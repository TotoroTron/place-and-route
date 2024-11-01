

module tb_top_level;

    localparam LENGTH = 24;
    reg tb_clk;
    reg tb_rst;
    reg tb_en;
    reg tb_din_valid;
    reg tb_din;
    wire [LENGTH-1:0] tb_dout;

    reg [LENGTH-1:0] tb_word;
    int num_errors = 0;
    reg tb_err;

    // instantiation unit under test 
    deserializer
    #(
        .LENGTH(LENGTH)
    ) dut (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
        .i_en(tb_en),
        .i_din_valid(tb_din_valid),
        .i_din(tb_din),
        .ov_dout(tb_dout)
    );


    task assert_and_report(input [LENGTH-1:0] expected, input [LENGTH-1:0] actual);
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
        tb_en = 1;
        tb_rst = 0;
        // serialize word, LSB first in
        for (int i = 0; i < LENGTH; i++) begin
            tb_din = word[i];
            if (i == LENGTH-1) begin
                tb_din_valid = 1;
            end
            @(posedge tb_clk);
        end
        // read output
        @(posedge tb_clk);
        assert_and_report(word, tb_dout);
        tb_din_valid = 0;
        tb_en = 0;
        tb_rst = 1; // reset the dut
        @(posedge tb_clk);
        tb_rst = 0;
    end
    endtask // test_word


    always #5 tb_clk = ~tb_clk;
    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;

        num_errors = 0;
        tb_clk = 1;
        tb_rst = 1;
        tb_en = 0;

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
