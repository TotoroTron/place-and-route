module matmul_systolic
#(
    parameter DATA_WIDTH = 8,
    parameter M = 4,
    parameter N = 4,
    parameter K = 4
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire [DATA_WIDTH-1:0] iv_a [M-1:0],
    input wire [DATA_WIDTH-1:0] iv_b [N-1:0]
);

    generate
    for (i = 0; i < M; i = i + 1) begin : gen_systolic_array
        generate
        for (j = 0; j < N; j = j + 1) begin
            mac_systolic
            #(
                .DATA_WIDTH(DATA_WIDTH)
            ) mac_inst (
                .i_clk(i_clk),
                .i_rst(i_rst),
                .i_en(i_en),
                .iv_a(iv_a[i]),
                .iv_b(iv_b[j])
            );
        end
        endgenerate
    endgenerate

endmodule
