package dev.jgluciano.fsm;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "dev.jgluciano.fsm", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule core_nao_depende_de_frameworks_nem_de_infraestrutura =
            noClasses().that().resideInAPackage("..core..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            "org.hibernate..",
                            "..infrastructure..")
                    .because("o core é Java puro (Clean Architecture)");

    @ArchTest
    static final ArchRule model_nao_depende_de_usecase_nem_ports =
            noClasses().that().resideInAPackage("..core.model..")
                    .should().dependOnClassesThat().resideInAnyPackage("..core.usecase..", "..core.ports..")
                    .because("o domínio não conhece quem o orquestra");

    @ArchTest
    static final ArchRule web_nao_acessa_persistence_diretamente =
            noClasses().that().resideInAPackage("..infrastructure.web..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure.persistence..")
                    .because("controllers falam com casos de uso, não com repositórios");

    @ArchTest
    static final ArchRule modulos_so_conversam_pela_api =
            slices().matching("dev.jgluciano.fsm.modules.(*)..")
                    .should().notDependOnEachOther()
                    .ignoreDependency(DescribedPredicate.alwaysTrue(), resideInAPackage("..modules.*.api.."))
                    .because("módulos só se acessam pelo pacote api");
}
