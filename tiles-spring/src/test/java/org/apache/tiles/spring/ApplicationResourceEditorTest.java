package org.apache.tiles.spring;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.junit.Before;
import org.junit.Test;

public class ApplicationResourceEditorTest {

    private ApplicationResourceEditor testTarget;
    private ApplicationContext applicationContext;
    
    @Before
    public void setUp() {
        applicationContext = createMock(ApplicationContext.class);
        testTarget = new ApplicationResourceEditor(applicationContext, false);
    }
    
    @Test
    public void testSetAsText() {
        ApplicationResource resource = createMock(ApplicationResource.class);
        expect(applicationContext.getResource("resource1")).andReturn(resource);
        replay(applicationContext, resource);
        testTarget.setAsText("resource1");
        assertEquals(resource, testTarget.getValue());
        verify(applicationContext, resource);
    }
    
    @Test
    public void testGetAsText() {
        ApplicationResource resource = createMock(ApplicationResource.class);
        expect(resource.getLocalePath()).andReturn("resource1");
        replay(applicationContext, resource);
        testTarget.setValue(resource);
        assertEquals("resource1", testTarget.getAsText());
        verify(applicationContext, resource);
    }
}
