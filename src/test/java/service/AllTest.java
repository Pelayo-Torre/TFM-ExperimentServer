package service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	service.InvestigatorTest.class,
	service.ExperimentTest.class,
	service.PetitionTest.class,
	service.BinnacleTest.class,
	service.AdministrationTest.class
})
public class AllTest {

}
