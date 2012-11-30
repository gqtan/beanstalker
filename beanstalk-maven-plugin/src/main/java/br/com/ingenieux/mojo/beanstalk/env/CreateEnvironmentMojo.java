package br.com.ingenieux.mojo.beanstalk.env;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.apache.commons.lang.StringUtils.isNotBlank;

import org.apache.commons.lang.Validate;
import org.apache.maven.plugin.AbstractMojoExecutionException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoSince;

import br.com.ingenieux.mojo.beanstalk.AbstractNeedsEnvironmentMojo;
import br.com.ingenieux.mojo.beanstalk.cmd.env.create.CreateEnvironmentCommand;
import br.com.ingenieux.mojo.beanstalk.cmd.env.create.CreateEnvironmentContext;
import br.com.ingenieux.mojo.beanstalk.cmd.env.create.CreateEnvironmentContextBuilder;

import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentResult;

/**
 * Creates and Launches an Elastic Beanstalk Environment
 * 
 * See the docs for the <a href=
 * "http://docs.amazonwebservices.com/elasticbeanstalk/latest/api/API_CreateEnvironment.html"
 * >CreateEnvironment API</a> call.
 * 
 */
@MojoGoal("create-environment")
@MojoSince("0.1.0")
public class CreateEnvironmentMojo extends AbstractNeedsEnvironmentMojo {
	@MojoParameter(expression="${beanstalk.environmentName}", description="Environment Name", defaultValue="${project.artifactId}-env")
	protected String environmentName;
        
	/**
	 * Application Description
	 */
	@MojoParameter(expression="${beanstalk.applicationDescription}", defaultValue="${project.name}")
	String applicationDescription;

	/**
	 * Configuration Option Settings
	 */
	@MojoParameter
	ConfigurationOptionSetting[] optionSettings;

	/**
	 * Version Label to use. Defaults to Project Version
	 */
	@MojoParameter(expression="${beanstalk.versionLabel}", defaultValue="${project.version}", required=true)
	String versionLabel;

	/**
	 * Solution Stack Name
	 */
	@MojoParameter(expression="${beanstalk.solutionStack}", defaultValue="32bit Amazon Linux running Tomcat 7")
	String solutionStack;

	/**
	 * Template Name
	 */
	@MojoParameter(expression="${beanstalk.templateName}")
	String templateName;

	/**
	 * Overrides parent in order to avoid a thrown exception as there's not an environment to lookup
	 */
	@Override
	protected void configure() {
		// Disable parent lookup - We're CREATING, mind that!
	}
	
	@Override
	protected Object executeInternal() throws AbstractMojoExecutionException {
		CreateEnvironmentResult result = createEnvironment(cnamePrefix, this.environmentName);

		return result;
	}

	protected CreateEnvironmentResult createEnvironment(String cnameToCreate, String newEnvironmentName)
	    throws AbstractMojoExecutionException {
		/*
		 * Hey Aldrin, have you ever noticed we're getting pedantic on those validations?
		 */
		Validate.isTrue(isNotBlank(newEnvironmentName), "No New Environment Name Supplied");
		
		CreateEnvironmentContextBuilder builder = CreateEnvironmentContextBuilder
		    .createEnvironmentContext() //
		    .withApplicationName(applicationName)//
		    .withApplicationDescription(applicationDescription)//
		    .withCnamePrefix(cnameToCreate)//
		    .withSolutionStack(solutionStack)//
		    .withTemplateName(templateName)//
		    .withEnvironmentName(newEnvironmentName)//
		    .withOptionSettings(optionSettings)//
		    .withVersionLabel(versionLabel);//
		
		CreateEnvironmentContext context = builder.build();

		CreateEnvironmentCommand command = new CreateEnvironmentCommand(this);

		return command.execute(context);
	}
}
