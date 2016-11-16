package com.baizhitong.util;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * 
 * @goal create
 * @phase process-sources
 */
public class CreateFolder extends AbstractMojo {

	/**
     * ��ȡԴ����·��
     * @parameter property="project.build.sourceDirectory"
     * @required
     */
    private File srcDirectory;
    
    /**
     * ��Ҫ���ɵ�package·��
     * @parameter property="package"
     * @required
     */
    private String packageName;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		try{
			mkdirs("dao");
		}catch(Exception e)
		{
			getLog().error("����:" + packageName + " Ŀ¼�쳣", e);
		}
	}

	/**
	 * ����Ŀ¼
	 * @param folder
	 */
	private void mkdirs(String folder){
		//����daoĿ¼
		File newFolder = new File(srcDirectory,packageName + File.separator + folder);
		newFolder.mkdir();
	}
}
