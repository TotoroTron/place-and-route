
module tb_top_level;

    localparam DATA_WIDTH = 16;
    reg tb_clk;
    reg tb_rst;
    reg tb_en;
    reg [DATA_WIDTH-1:0] tb_a;
    reg [DATA_WIDTH-1:0] tb_b;
    reg tb_cin;

    wire [DATA_WIDTH-1:0] cla_sum;
    wire cla_cout;

    int num_errors = 0;
    logic tb_err;
    reg [DATA_WIDTH:0] tb_exp_sum;
    reg tb_exp_cout;

    top_level
    #(
        .DATA_WIDTH(DATA_WIDTH)
    ) clas (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
        .i_en(tb_en),
        .iv_a(tb_a),
        .iv_b(tb_b),
        .i_cin(tb_cin),
        .ov_sum(cla_sum),
        .o_cout(cla_cout)
    );

    task assert_and_report(input [DATA_WIDTH-1:0] expected, input [DATA_WIDTH-1:0] actual);
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


    task test_add(input [DATA_WIDTH-1:0] sum, input cout);
    begin
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
endmodule
