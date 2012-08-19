package ru.kt15.finomen.neerc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ru.kt15.finomen.neerc.core.tests.AllTests.class,
	ru.kt15.finomen.neerc.hall.tests.AllTests.class})
public class AllTests {

}
