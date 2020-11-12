package com.dochi.labs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        //assertTrue( true );
        
        Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("WORK_YN", 1);
        updateMap.put("TITLE", "HI");        

        final StringBuilder sqlBuilder = new StringBuilder();
        final List<String> updateSeq = new ArrayList<String>();
        
        sqlBuilder.append("UPDATE FROM CW_BLOG_ATCL_LIST \n");
        sqlBuilder.append("SET ");
        sqlBuilder.append(
            updateMap.keySet().stream().reduce("", (prev, current)->{
                updateSeq.add(current);
                
                current = current + " = ? ";         
                if( !"".equals(prev) ) {
                    prev = prev+", ";
                }
                return prev+current+"\n";
            })      
        );
        sqlBuilder.append("WHERE 1=1 \n");
        sqlBuilder.append("  AND BLOG_ID = ? \n");
        sqlBuilder.append("  AND CATE_ID = ? \n");
        sqlBuilder.append("  AND ATCL_ID = ? \n");
        
        System.out.println(sqlBuilder.toString());
        System.out.println(updateSeq.toString());
    }
}
