/**
 * Simulation test for Giant_magic_cannon logic
 * Tests debt calculation and energy gate
 */
public class test_gmc_logic {
    
    static class GMCSimulator {
        private static final int MIN_ENERGY_TO_PLAY = 3;
        
        boolean isFreeToPlayOnce = false;
        boolean ignoreEnergyOnUse = false;
        int costForTurn = 5; // default
        
        boolean hasEnoughEnergy(int playerEnergy) {
            // Free/ignore: always playable
            if (this.isFreeToPlayOnce || this.ignoreEnergyOnUse) return true;
            
            // Cost 0: no energy check needed
            if (this.costForTurn <= 0) return true;
            
            // Cost > 0: need at least MIN_ENERGY_TO_PLAY
            if (playerEnergy < MIN_ENERGY_TO_PLAY) return false;
            
            // Either we have enough to pay full cost, or we can underpay
            if (playerEnergy >= this.costForTurn) return true;
            
            return playerEnergy >= MIN_ENERGY_TO_PLAY && playerEnergy < this.costForTurn;
        }
        
        int calculateDebt(int playerEnergy) {
            if (this.isFreeToPlayOnce || this.ignoreEnergyOnUse) return 0;
            if (this.costForTurn <= 0) return 0; // Cost 0 = no debt
            if (playerEnergy >= this.costForTurn) return 0; // Full pay = no debt
            
            return this.costForTurn - playerEnergy;
        }
    }
    
    public static void main(String[] args) {
        GMCSimulator gmc = new GMCSimulator();
        
        System.out.println("=== Giant Magic Cannon Logic Test ===\n");
        
        // Test 1: 1 energy → card grayed out
        System.out.println("Test 1: 1 energy, cost 5");
        gmc.costForTurn = 5;
        boolean canPlay1 = gmc.hasEnoughEnergy(1);
        System.out.println("  hasEnoughEnergy(1) = " + canPlay1);
        System.out.println("  Expected: false");
        System.out.println("  PASS: " + (!canPlay1) + "\n");
        
        // Test 2: 3 energy, cost 5 → debt = 2
        System.out.println("Test 2: 3 energy, cost 5");
        gmc.costForTurn = 5;
        boolean canPlay2 = gmc.hasEnoughEnergy(3);
        int debt2 = gmc.calculateDebt(3);
        System.out.println("  hasEnoughEnergy(3) = " + canPlay2);
        System.out.println("  debt = " + debt2);
        System.out.println("  Expected: true, debt = 2");
        System.out.println("  PASS: " + (canPlay2 && debt2 == 2) + "\n");
        
        // Test 3: Mummified Hand (cost = 0) → any energy playable, debt = 0
        System.out.println("Test 3: 1 energy, cost 0 (Mummified Hand)");
        gmc.costForTurn = 0;
        boolean canPlay3 = gmc.hasEnoughEnergy(1);
        int debt3 = gmc.calculateDebt(1);
        System.out.println("  hasEnoughEnergy(1) = " + canPlay3);
        System.out.println("  debt = " + debt3);
        System.out.println("  Expected: true, debt = 0");
        System.out.println("  PASS: " + (canPlay3 && debt3 == 0) + "\n");
        
        // Test 4: 3 energy + Contempt (cost reduced to 4) → debt = 1
        System.out.println("Test 4: 3 energy, cost reduced to 4");
        gmc.costForTurn = 4;
        boolean canPlay4 = gmc.hasEnoughEnergy(3);
        int debt4 = gmc.calculateDebt(3);
        System.out.println("  hasEnoughEnergy(3) = " + canPlay4);
        System.out.println("  debt = " + debt4);
        System.out.println("  Expected: true, debt = 1");
        System.out.println("  PASS: " + (canPlay4 && debt4 == 1) + "\n");
        
        // Test 5: 3 base + Happy Flower = 4, cost 5 → debt = 1
        System.out.println("Test 5: 4 energy (3 + Happy Flower), cost 5");
        gmc.costForTurn = 5;
        boolean canPlay5 = gmc.hasEnoughEnergy(4);
        int debt5 = gmc.calculateDebt(4);
        System.out.println("  hasEnoughEnergy(4) = " + canPlay5);
        System.out.println("  debt = " + debt5);
        System.out.println("  Expected: true, debt = 1");
        System.out.println("  PASS: " + (canPlay5 && debt5 == 1) + "\n");
        
        // Test 6: 2 energy → card grayed out
        System.out.println("Test 6: 2 energy, cost 5");
        gmc.costForTurn = 5;
        boolean canPlay6 = gmc.hasEnoughEnergy(2);
        System.out.println("  hasEnoughEnergy(2) = " + canPlay6);
        System.out.println("  Expected: false");
        System.out.println("  PASS: " + (!canPlay6) + "\n");
        
        // Summary
        int totalTests = 6;
        int passedTests = 0;
        passedTests += !canPlay1 ? 1 : 0;
        passedTests += (canPlay2 && debt2 == 2) ? 1 : 0;
        passedTests += (canPlay3 && debt3 == 0) ? 1 : 0;
        passedTests += (canPlay4 && debt4 == 1) ? 1 : 0;
        passedTests += (canPlay5 && debt5 == 1) ? 1 : 0;
        passedTests += !canPlay6 ? 1 : 0;
        
        System.out.println("=== SUMMARY ===");
        System.out.println("Passed: " + passedTests + "/" + totalTests);
        if (passedTests == totalTests) {
            System.out.println("ALL TESTS PASSED ✓");
        } else {
            System.out.println("SOME TESTS FAILED ✗");
        }
    }
}
