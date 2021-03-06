package hudson.plugins.mstest;

import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;

/**
 * Unit tests for MSTestTransformer class
 * 
 * @author Antonio Marques
 */
public class MSTestTransformerAndConverterTest extends TestHelper{

	
    private BuildListener buildListener;
    private Mockery context;
    private Mockery classContext;
    private MSTestTransformer transformer;
    private VirtualChannel virtualChannel;

    
    @Before
    public void setUp() throws Exception {
        createWorkspace();

        context = getMock();
        classContext = getClassMock();
        buildListener = classContext.mock(BuildListener.class);
        virtualChannel = context.mock(VirtualChannel.class);
    }

    @After
    public void tearDown() throws Exception {
    	   deleteWorkspace();
    }

    @Test
    public void testInvalidXmlCharacters() throws Exception {
        final PrintStream logger = new PrintStream(new ByteArrayOutputStream());
        classContext.checking(new Expectations() {
            {
                ignoring(buildListener).getLogger();
                will(returnValue(logger));
                ignoring(buildListener);
            }
        });
        final String testPath = "xmlentities-forged.xml";
        File testFile = new File(parentFile, testPath);
        if (testFile.exists())
            testFile.delete();
        InputStream testStream = this.getClass().getResourceAsStream("JENKINS-23531-xmlentities-forged.trx");
        Files.copy(testStream, testFile.toPath());
        MSTestReportConverter converter = new MSTestReportConverter();
        transformer = new MSTestTransformer(testPath, converter, buildListener);
        transformer.invoke(parentFile, virtualChannel);
    }
}
