----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 07/28/2024 02:59:44 PM
-- Design Name: 
-- Module Name: top_level - structural
-- Project Name: 
-- Target Devices: 
-- Tool Versions: 
-- Description: 
-- 
-- Dependencies: 
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
-- 
----------------------------------------------------------------------------------


library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity top_level is
    Port (
        i_clk : in std_logic;
        i_rst : in std_logic;
        ov_led : out std_logic_vector (3 downto 0)
    );
end top_level;

architecture structural of top_level is
    signal en : std_logic;
begin

    CLOCK_ENABLE_GEN : entity work.pps_gen
    generic map(pulse_freq => 4) -- pulses per second
    port map(
        i_clk => i_clk,
        i_rst => i_rst,
        o_pulse => en
    );

    COUNTER : entity work.counter
    generic map(width => 4)
    port map(
        i_clk => i_clk,    
        i_rst => i_rst,
        i_en => en,
        ov_count => ov_led
    );

end structural;

