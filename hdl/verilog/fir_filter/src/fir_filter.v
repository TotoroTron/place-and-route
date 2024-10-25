module fir_filter 
    #(
        parameter DATA_WIDTH = 24,
        parameter FIR_DEPTH = 128
    )
    (
        input wire i_clk,
        input wire i_rst,
        input wire i_en,
        input wire [DATA_WIDTH-1:0] iv_din,
        output wire [DATA_WIDTH-1:0] ov_dout
    );

    always @(posedge i_clk) begin
        if (i_rst) begin
        end else begin
            if (i_en) begin

            end
        end 
    end


endmodule
