package com.superiorbank;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    static final ApplicationModules modules = ApplicationModules.of(SuperiorBankApplication.class);

    /**
     * This test verifies that no module has violated another module's boundaries.
     * If the notification module accidentally imports a class from
     * transaction/internal/, this test FAILS with a clear error message.
     *
     * Run this in CI. Catch accidental coupling before it merges.
     */
    @Test
    void moduleStructureIsValid() {
        modules.verify();
    }

    /**
     * Generates architectural documentation as PlantUML and AsciiDoc.
     * Your architecture diagrams are always in sync with your code.
     * No more "the diagram was last updated 2 years ago" conversations.
     */
    @Test
    void generateDocumentation() {
        new Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml()
            .writeAggregatingDocument();
    }

    @Test
    void createApplicationModuleModel() {
        modules.forEach(System.out::println);
    }

    @Test
    void createModuleDocumentation() {
      ApplicationModules modules = ApplicationModules.of(SuperiorBankApplication.class);
      new Documenter(modules)
      .writeDocumentation()
      .writeIndividualModulesAsPlantUml();
    }
}
