
module mult_booth_sync 
#(
    parameter DATA_WIDTH = 16
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire [DATA_WIDTH-1:0] iv_a,
    input wire [DATA_WIDTH-1:0] iv_b,
    output reg [DATA_WIDTH*2-1:0] ov_prod
);

    reg [DATA_WIDTH-1:0] a;
    reg [DATA_WIDTH-1:0] b;
    wire [DATA_WIDTH*2-1:0] comb_prod;

    always @(posedge i_clk) begin
        a <= a;
        b <= b;
        ov_prod <= ov_prod;
        if (i_rst) begin
            a <= 0;
            b <= 0;
            ov_prod <= 0;
        end else if (i_en) begin
            a <= iv_a;
            b <= iv_b;
            ov_prod <= comb_prod;
        end
    end

    mult_booth
    #(
        .DATA_WIDTH(DATA_WIDTH)
    ) mult_booth_inst (
        .A(a),
        .B(b),
        .z(comb_prod)
    );

endmodule
