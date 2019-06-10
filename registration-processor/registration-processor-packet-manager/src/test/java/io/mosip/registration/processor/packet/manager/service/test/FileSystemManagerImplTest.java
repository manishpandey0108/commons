package io.mosip.registration.processor.packet.manager.service.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.packet.manager.PacketManagerBootApplication;
import io.mosip.registration.processor.packet.manager.decryptor.Decryptor;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;

/**
 * FileSystemManagerImpl test
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@SpringBootTest(classes = PacketManagerBootApplication.class)
@RunWith(SpringRunner.class)
public class FileSystemManagerImplTest {

	@Autowired
	private PacketManager packetManager;

	@MockBean
	private Decryptor decryptorImpl;

	@MockBean
	private FileSystemAdapter fsAdapter;

	private InputStream zipFile;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		zipFile = this.getClass().getClassLoader().getResourceAsStream("12345678901234567890.zip");
	}

	@Test
	public void getPacketSuccess() throws IOException, PacketDecryptionFailureException, ApisResourceAccessException,
			io.mosip.kernel.core.exception.IOException {
		when(fsAdapter.getPacket(anyString())).thenReturn(IOUtils.toInputStream("DATA", "UTF-8"));
		when(decryptorImpl.decrypt(any(), anyString())).thenReturn(IOUtils.toInputStream("DATA", "UTF-8"));
		packetManager.getPacket("12345678901234567890");
	}

	@Test(expected = FileNotFoundInDestinationException.class)
	public void getPacketPacketNoFound() throws IOException, PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		when(decryptorImpl.decrypt(any(), anyString())).thenReturn(IOUtils.toInputStream("DATA", "UTF-8"));
		packetManager.getPacket("12345678901234567890");
	}

	@Test(expected = PacketDecryptionFailureException.class)
	public void getPacketDecryptionFailed() throws IOException, PacketDecryptionFailureException,
			ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		when(fsAdapter.getPacket(anyString())).thenReturn(IOUtils.toInputStream("DATA", "UTF-8"));
		packetManager.getPacket("12345678901234567890");
	}

	@Test
	public void getFileSuccess() throws IOException, PacketDecryptionFailureException, ApisResourceAccessException,
			io.mosip.kernel.core.exception.IOException {
		InputStream inputStream = IOUtils.toInputStream("DATA", "UTF-8");
		when(fsAdapter.getPacket(Mockito.anyString())).thenReturn(inputStream);
		when(decryptorImpl.decrypt(any(), anyString())).thenReturn(zipFile);
		InputStream result = packetManager.getFile("12345678901234567890", "DEMOGRAPHIC/ID");
		assertNotNull(result);

	}

	@Test
	public void isFileExistSuccess() throws IOException, PacketDecryptionFailureException, ApisResourceAccessException,
			io.mosip.kernel.core.exception.IOException {
		InputStream inputStream = IOUtils.toInputStream("DATA", "UTF-8");
		when(fsAdapter.getPacket(Mockito.anyString())).thenReturn(inputStream);
		when(decryptorImpl.decrypt(any(), anyString())).thenReturn(zipFile);
		boolean result = packetManager.checkFileExistence("12345678901234567890", "DEMOGRAPHIC/ID");
		assertTrue(result);

	}

	@Test
	public void storeFilePacketSuccess() {
		when(fsAdapter.storePacket(Mockito.anyString(), Mockito.any(File.class))).thenReturn(true);
		boolean result = packetManager.storePacket("12345678901234567890", new File("12345678901234567890"));
		assertTrue(result);
	}

	@Test(expected = FSAdapterException.class)
	public void storeFileFailure() {
		when(fsAdapter.storePacket(Mockito.anyString(), Mockito.any(File.class))).thenThrow(FSAdapterException.class);
		packetManager.storePacket("12345678901234567890", new File("12345678901234567890"));
	}

	@Test
	public void storeFilePacket() {
		when(fsAdapter.storePacket(Mockito.anyString(), Mockito.any(File.class))).thenReturn(true);
		boolean result = packetManager.storePacket("12345678901234567890", new File("12345678901234567890"));
		assertTrue(result);
	}

	@Test(expected = FSAdapterException.class)
	public void storeFilePacketFailure() {
		when(fsAdapter.storePacket(Mockito.anyString(), Mockito.any(File.class))).thenThrow(FSAdapterException.class);
		packetManager.storePacket("12345678901234567890", new File("12345678901234567890"));
	}

	@Test
	public void storePacketSuccess() throws IOException {
		InputStream inputStream = IOUtils.toInputStream("DATA", "UTF-8");
		when(fsAdapter.storePacket(Mockito.anyString(), Mockito.any(InputStream.class))).thenReturn(true);
		boolean result = packetManager.storePacket("12345678901234567890", inputStream);
		assertTrue(result);
	}

	@Test(expected = FSAdapterException.class)
	public void storePacketFailure() throws IOException {
		InputStream inputStream = IOUtils.toInputStream("DATA", "UTF-8");
		when(fsAdapter.storePacket(Mockito.anyString(), Mockito.any(InputStream.class)))
				.thenThrow(FSAdapterException.class);
		packetManager.storePacket("12345678901234567890", inputStream);
	}

}
