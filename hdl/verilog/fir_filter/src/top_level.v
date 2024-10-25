module top_level
#(parameter LENGTH = 32)
    (
        input wire i_clk,
        input wire i_rst,
        input wire i_din,
        output wire o_dout
    );

    shift_reg #(.LENGTH(LENGTH)) shift_reg_0 (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_din(i_din),
        .o_dout(o_dout)
    );

endmodule
