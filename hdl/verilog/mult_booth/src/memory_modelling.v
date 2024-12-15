module memory_modelling
#(
    DATA_WIDTH = 16
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire [DATA_WIDTH-1:0] iv_b
    output wire [DATA_WIDTH-1:0] ov_sum
)


