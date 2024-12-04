module top_level
#(parameter DATA_WIDTH = 16)
    (
        input wire i_clk,
        input wire i_rst,
        input wire i_en,
        input wire i_a,
        input wire i_b,
        input wire i_cin,
        output wire o_sum,
        output wire o_cout
    );

    wire cin = 0;

    deserializer_fsm
    #(
        .LENGTH(DATA_WIDTH)
    ) deserializer_inst_a (
        .i_clk(),
        .i_rst(),
        .i_en(),
        .i_din(),
        .i_din_valid(),
        .i_ready(),
        .o_ready(),
        .ov_dout(),
        .o_dout_valid()
    );

    deserializer_fsm
    #(
        .LENGTH(DATA_WIDTH)
    ) deserializer_inst_b (
        .i_clk(),
        .i_rst(),
        .i_en(),
        .i_din(),
        .i_din_valid(),
        .i_ready(),
        .o_ready(),
        .ov_dout(),
        .o_dout_valid()
    );

    carry_lookahead_adder
    #(
        .DATA_WIDTH(DATA_WIDTH)
    ) clas (
        .iv_a(a),
        .iv_b(b),
        .i_cin(cin),
        .ov_sum(sum),
        .o_cout(cout)
    );


    serializer_fsm
    #(
        .LENGTH(DATA_WIDTH)
    ) serializer_inst (
        .i_clk(),
        .i_rst(),
        .i_en(),
        .iv_din(),
        .i_din_valid(),
        .i_ready(),
        .o_ready(),
        .o_dout(),
        .o_dout_valid()
    );

endmodule
