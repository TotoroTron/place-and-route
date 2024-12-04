
module tb_top_level;

    localparam DATA_WIDTH = 16;

    reg tb_clk;
    reg tb_rst;
    reg tb_en;
    reg [DATA_WIDTH-1:0] tb_a;
    reg [DATA_WIDTH-1:0] tb_b;
    reg tb_cin;
    reg tb_din_a;
    reg tb_din_b;
    reg tb_din_valid;
    reg tb_ready;
    wire dut_ready;
    wire tb_dout;
    wire tb_dout_valid;


    wire [DATA_WIDTH-1:0] cla_sum;
    wire cla_cout;

    int num_errors = 0;
    reg [DATA_WIDTH:0] tb_exp_sum;
    reg tb_exp_cout;
    reg [DATA_WIDTH-1:0] tb_word_in;
    reg [DATA_WIDTH-1:0] serial_word;
    reg [DATA_WIDTH-1:0] tb_word_out;

    top_level
    #(
        .DATA_WIDTH(DATA_WIDTH)
    ) clas (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
        .i_en(tb_en),
        .i_din_a(tb_din_a),
        .i_din_b(tb_din_b),
        .i_cin(tb_cin),
        .i_valid(tb_din_valid),
        .i_ready(tb_ready),
        .o_ready(dut_ready),
        .o_sum(tb_dout),
        .o_cout(tb_cout),
        .o_valid(tb_dout_valid)
    );

    task assert_and_report(input [DATA_WIDTH-1:0] expected, input [DATA_WIDTH-1:0] actual);
    begin
        if (actual == expected) begin
            $display(" SUCCESS!\n  Expected: %b\n  Actual:   %b", expected, actual);
        end else begin
            $display(" FAILURE!\n  Expected: %b\n  Actual:   %b", expected, actual);
            num_errors = num_errors + 1;
        end
    end
    endtask // assert_and_report


    task test_add(input [DATA_WIDTH-1:0] sum, input cout);
    begin
        tb_din_valid = 0;
        @(posedge tb_clk);
        tb_en = 1;
        tb_rst = 0;
        tb_din_valid = 1;

        wait (dut_ready == 1'b1);
        @(posedge tb_clk);

        // serialize word, LSB first in
        for (int i = 0; i < DATA_WIDTH; i++) begin
            tb_din_a = tb_a[i];
            tb_din_b = tb_b[i];
            @(posedge tb_clk);
        end

        tb_din_valid = 0;
        wait(tb_dout_valid == 1'b1); // waits for dout valid from dut
        tb_ready = 1; // consume dout from dut

        @(posedge tb_clk);
        tb_ready = 0;

        // arbitrary wait
        for (int i = 0; i < 50; i++) begin
            @(posedge tb_clk);
        end

        {tb_exp_cout, tb_exp_sum} = {1'b0, tb_a} + {1'b0, tb_b} + { {(DATA_WIDTH){1'b0}}, tb_cin };
        assert_and_report( {tb_exp_cout, tb_exp_sum}, {cout, sum} );
    end
    endtask

    always #5 tb_clk = ~tb_clk;
    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;

        num_errors = 0;
        tb_clk = 1;
        tb_rst = 1;
        tb_en = 0;

        @(posedge tb_clk);
        tb_rst = 0;
        tb_en = 1;
        @(posedge tb_clk);

        repeat (100) begin
            tb_a = $urandom();
            tb_b = $urandom();
            tb_cin = $urandom();
            @(posedge tb_clk);
            @(posedge tb_clk);

            $display("A: %h, B: %h, CIN: %h", tb_a, tb_b, tb_cin);
            $display("CLA: ");
            test_add(cla_sum, cla_cout);
            $display();
        end

        $display();
        $display("Total number of errors: %d", num_errors);
        $display();

        $finish;
    end // initial

    always begin
        @(posedge tb_clk);
        wait(tb_dout_valid == 1'b1);
        // @(posedge tb_clk iff(dut_ready == 1'b1));
        tb_ready = 1;
        @(posedge tb_clk);
        for (int i = 0; i < DATA_WIDTH; i++) begin
            serial_word[i] = tb_dout;
            @(posedge tb_clk);
        end
        tb_ready = 0;
        tb_word_out = serial_word;
        @(posedge tb_clk);
    end // always

    initial begin
        #20ms;
        $display("Simulation terminated after 20 milliseconds.");
        $finish;
    end // initial

endmodule
