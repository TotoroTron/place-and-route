module carry_lookahead_adder_sync
#(
    parameter DATA_WIDTH = 16
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire [DATA_WIDTH-1:0] iv_a,
    input wire [DATA_WIDTH-1:0] iv_b,
    input wire i_cin,
    output reg [DATA_WIDTH-1:0] ov_sum,
    output reg o_cout
);

    wire [DATA_WIDTH-1:0] comb_sum;
    wire comb_cout;

    always @(posedge i_clk) begin
        ov_sum <= ov_sum;
        o_cout <= o_cout;
        if (i_rst) begin
            ov_sum <= 0;
            o_cout <= 1'b0;
        end else if (i_en) begin
            ov_sum <= comb_sum;
            o_cout <= comb_cout;
        end
    end

    carry_lookahead_adder
    #(
        .DATA_WIDTH(DATA_WIDTH)
    ) cla (
        .iv_a(iv_a),
        .iv_b(iv_b),
        .i_cin(i_cin),
        .ov_sum(comb_sum),
        .o_cout(comb_cout)
    );

endmodule
