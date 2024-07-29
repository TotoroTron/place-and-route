----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 07/29/2024 06:39:54 PM
-- Design Name: 
-- Module Name: testbench - Behavioral
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


library ieee;
use ieee.std_logic_1164.all;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity testbench is
--  Port ( );
end testbench;

architecture behavioral of testbench is
    signal tb_clk : std_logic := '0';
    signal tb_rst : std_logic := '0';
    signal tb_led : std_logic_vector(3 downto 0) := (others => '0');
    constant clk_period : time := 10ns;
begin
    
    UUT : entity work.top_level
    port map(
        clk => tb_clk,
        rst => tb_rst,
        led => tb_led
    );

    CLOCK_GEN : process
    begin
        tb_clk <= '0';
        wait for clk_period/2;
        tb_clk <= '1';
        wait for clk_period/2;
    end process;
    -- https://stackoverflow.com/questions/17904514/vhdl-how-should-i-create-a-clock-in-a-testbench

    STIMULI : process
    begin
        wait for 25 ns;
        rst <= '1';
        wait for 25 ns;
        rst <= '0';

        wait for 1000000 ns;
        rst <= '1';
        wait for 1000000 ns;
        rst <= '0';


    end process;

end behavioral;
