

module serializer_fsm
#(
    parameter LENGTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire [LENGTH-1:0] iv_din,
    input wire i_din_valid,
    input wire i_ready,
    output reg o_ready,
    output wire o_dout,
    output reg o_dout_valid
);

    reg [LENGTH-1:0] shift_reg;
    assign o_dout = shift_reg[0];

    localparam LENGTH_BITS = $clog2(LENGTH)+1;
    reg [LENGTH_BITS-1:0] counter = { (LENGTH){1'b0} };

    parameter S0 = 4'b0000,
        S1 = 4'b0001,
        S2 = 4'b0010,
        S3 = 4'b0011;
    reg [3:0] state = S0;
    reg [3:0] next_state;


    // STATE REGISTER
    always @(posedge i_clk) begin
        if (i_rst) state <= S0;
        else if (i_en) state <= next_state;
    end

    // STATE MACHINE
    always @(*) begin
        case (state)
            S0: begin
                next_state <= S0;
                // WAIT FOR INPUT DATA VALID
                if (i_din_valid)
                    next_state <= S1;
            end
            S1: begin
                next_state <= S2;
            end
            S2: begin
                next_state <= S3;
            end
            S3: begin
                next_state <= S3;
                // DATA SHIFT
                if (counter == LENGTH)
                    next_state <= S0;
            end
            default: next_state <= S0;
        endcase
    end

    // OUTPUT LOGIC
    always @(posedge i_clk) begin
        if (i_rst) begin
            o_ready <= 1'b0;
            o_dout_valid <= 1'b0;
        end else if (i_en) begin
            o_dout_valid <= 1'b0;
            o_ready <= 1'b0;
            case (state) 
                S0: begin
                    // WAIT FOR DIN VALID
                    shift_reg <= 0;
                end
                S1: begin
                    // SIGNAL DIN CONSUMED
                    o_ready <= 1'b1;
                    shift_reg <= iv_din;
                end
                S2: begin
                    // SIGNAL DOUT VALID
                    o_dout_valid <= 1'b1;
                end
                S3: begin
                    // DATA SHIFT
                    if (i_ready && counter < LENGTH) begin
                        o_dout_valid <= 1'b1;
                        shift_reg <= { 1'b0, shift_reg[LENGTH-1:1] };
                        counter <= counter + 1;
                    end else begin
                        o_dout_valid <= 1'b0;
                        counter <= 0;
                    end
                end
                default: begin
                end
            endcase
        end
    end
endmodule
