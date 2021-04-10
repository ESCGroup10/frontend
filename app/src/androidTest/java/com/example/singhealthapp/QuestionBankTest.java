package com.example.singhealthapp;

import android.content.Context;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.singhealthapp.Containers.AuditorFragmentContainer;
import com.example.singhealthapp.HelperClasses.QuestionBank;
import com.example.singhealthapp.Views.Auditor.Checklists.AuditChecklistFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

public class QuestionBankTest {
    //private Context context = getInstrumentation().getTargetContext();
    QuestionBank qb;
    ArrayList<String> expected;
    private Context context;
    FragmentScenario<AuditChecklistFragment> fragmentScenario;

    @Before
    public void setUp() {

        qb = new QuestionBank(context);
        expected = new ArrayList<String>();
        expected.add("-Professionalism");
        expected.add("Shop is open and ready to service patients/visitors according to operating hours.");
        expected.add("Staff Attendance: adequate staff for peak and non-peak hours.");
        expected.add("At least one (1) clearly assigned person in-charge on site.");
        expected.add("-Staff Hygiene");
        expected.add("Staff uniform/attire is not soiled.");
        expected.add("Staff who are unfit for work due to illness should not report to work).");
        expected.add("Staff who are fit for work but suffering from the lingering effects of a cough and/or cold should cover their mouths with a surgical mask.");
    }

    @Test
    public void TestOutput_QuestionBank() {
        ArrayList<String> actual = qb.getQuestions("Non_F&B_professionalism_and_staff_hygiene");
        assertEquals(expected, actual);
    }

}
