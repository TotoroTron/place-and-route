

module deserializer_fsm
#(
    parameter LENGTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_din,
    input wire i_din_valid,
    input wire i_ready, // from fir: fir ready or not
    output reg o_ready, // to testbench: deserializer ready or not
    output reg [LENGTH-1:0] ov_dout,
    output reg o_dout_valid
);

    reg [LENGTH-1:0] shift_reg;
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
                // WAIT FOR DIN VALID
                if (i_din_valid)
                    next_state <= S1;
            end
            S1: begin
                next_state <= S2;
            end
            S2: begin
                next_state <= S2;
                // DATA SHIFT
                if (counter == LENGTH)
                    next_state <= S3;
            end
            S3: begin
                next_state <= S3;
                // WAIT FOR RECEIVER TO CONSUME OUTPUT DATA
                if (i_ready)
                    next_state <= S0;
            end
            default: next_state <= S0;
        endcase
    end

    // OUTPUT LOGIC
    always @(posedge i_clk) begin
        o_ready <= 1'b0;
        o_dout_valid <= 1'b0;
        if (i_rst) begin
            o_ready <= 1'b0;
            o_dout_valid <= 1'b0;
            counter <= 0;
            shift_reg <= 0;
        end else if (i_en) begin
            case (state)
                S0: begin
                    // WAIT FOR DIN VALID
                    shift_reg <= 0;
                    counter <= 0;
                end
                S1: begin
                    o_ready <= 1'b1;
                end
                S2: begin
                    // SIGNAL DIN BEING CONSUMED
                    o_ready <= 1'b1;
                    if (i_din_valid == 1'b1 && counter < LENGTH) begin
                        shift_reg <= { i_din, shift_reg[LENGTH-1:1] };
                        counter <= counter + 1;
                    end else begin
                        counter <= 0;
                        ov_dout <= shift_reg;
                    end
                end
                S3: begin
                    // WAIT FOR RECEIVER TO CONSUME DOUT
                    o_dout_valid <= 1'b1;
                end
                default: begin
                end
            endcase
        end
    end
endmodule
