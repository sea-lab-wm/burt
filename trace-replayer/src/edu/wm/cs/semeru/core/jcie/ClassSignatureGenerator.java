/*******************************************************************************
 * Copyright (c) 2016, SEMERU
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
package edu.wm.cs.semeru.core.jcie;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.wm.cs.semeru.core.jcie.helper.FileHelper;
import edu.wm.cs.semeru.core.jcie.model.AttributeVO;
import edu.wm.cs.semeru.core.jcie.model.ClassInformationVO;
import edu.wm.cs.semeru.core.jcie.model.MethodInformationVO;
import edu.wm.cs.semeru.core.jcie.model.StatementLocation;
import edu.wm.cs.semeru.core.jcie.visitor.ConstructorInvocationVisitor;
import edu.wm.cs.semeru.core.jcie.visitor.MethodInvocationVisitor;
import edu.wm.cs.semeru.core.jcie.visitor.TypeDeclarationVisitor;



public class ClassSignatureGenerator {

	public static String FIELD_SEPARATOR = ";";
	public static String METHOD_SEPARATOR_IN_PATTERN = "---";
	
	
	
	private static String getVisibility(int flag){
		if(Modifier.isPrivate(flag)){
			return "private";
		} else if(Modifier.isPublic(flag)){
			return "public";
		} 
		else if(Modifier.isProtected(flag)){
			return "protected";
		}else{
			return "default";	
		} 
	}
	
	public static ClassInformationVO getClassInfo(String javaFilePath, String binariesFolder, String sourceFolder) throws IOException{
		ClassInformationVO classInfo = new ClassInformationVO();
		String fileContent = "";
		List<String> jars = getJarsInfolder(binariesFolder);
		String[] classPath = new String[jars.size()];
		//---"/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar
	    String unitName =  javaFilePath.substring( javaFilePath.lastIndexOf(File.separator)+1, javaFilePath.lastIndexOf(".java") );
	    //Visitors
	    MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor();
  		ConstructorInvocationVisitor constructorInvocationVisitor = new ConstructorInvocationVisitor();
  		TypeDeclarationVisitor typeDeclarationVisitor = new TypeDeclarationVisitor();
  		
  		
		for(int i = 0; i < classPath.length; i++){
			classPath[i] = binariesFolder+File.separator+jars.get(i);
		}
		String[] srcPath = {sourceFolder};
	    
		//Getting file content from .java file
	    fileContent = FileHelper.readFile(javaFilePath);
		
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setSource(fileContent.toCharArray());
		Hashtable<String, String> options = JavaCore.getDefaultOptions();
	    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
	    parser.setCompilerOptions(options);
	    parser.setUnitName(unitName);
	    parser.setEnvironment(classPath, srcPath, null, false);
	    parser.setKind(ASTParser.K_COMPILATION_UNIT);
	    
	    
	    //File compilation  		
  		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
  		cu.accept(typeDeclarationVisitor);
  		
  		IProblem[] problems = cu.getProblems();
  	    if (problems != null && problems.length > 0) {
  	        System.out.println("Got {} problems compiling the source file: "+ problems.length);
  	        for (IProblem problem : problems) {
  	        	System.out.println("{} "+ problem);
  	        }
  	    }
  	    
  		//Traversing the Class' AST
  	    List<TypeDeclaration> classes = typeDeclarationVisitor.getClasses();
  		 
  		
  		
  		for (TypeDeclaration node : classes) {
  			ITypeBinding binding = node.resolveBinding();
  			
  			//System.out.println("-- Getting Class info for " + binding.getQualifiedName());
  			if(binding != null){
  				
  				classInfo.setName(binding.getName());
  				classInfo.setPackageName( binding.getPackage().getName());
  				classInfo.setVisibility(getVisibility(binding.getModifiers()));
  			
  				ITypeBinding[] interfaces = binding.getInterfaces();
  				for (ITypeBinding iTypeBinding : interfaces) {
  					classInfo.getInterfaces().add(iTypeBinding.getName());
  				}
  			}
  			else{
  				classInfo.setName(node.getName().toString());
  				classInfo.setVisibility(getVisibility(node.getModifiers()));
  			}
  			
  			
  			if (binding != null && binding.getSuperclass() != null && !binding.getSuperclass().getName().isEmpty()){
  			
  				classInfo.setSuperClass(binding.getSuperclass().getName());
  			}
  			//else{
  			//	classInfo.setSuperClass(node.getSuperclass().toString());
  			//}
  			
  			MethodDeclaration[] declaredMethods = node.getMethods();
  			for (MethodDeclaration method : declaredMethods) {
  				IMethodBinding mBinding = method.resolveBinding();
				MethodInformationVO methodInfo = new MethodInformationVO();
				if(method.isConstructor()){
					methodInfo.setName("<init>");
				}
				else{
					methodInfo.setName(method.getName().toString());
				}

				if(mBinding != null && mBinding.getReturnType() != null && !mBinding.getReturnType().getName().isEmpty()){
				methodInfo.setReturnType(mBinding.getReturnType().getName());

				if(mBinding.getReturnType() != null){
				    methodInfo.setReturnType(mBinding.getReturnType().getName());
				}

				methodInfo.setVisibility(getVisibility(mBinding.getModifiers()));
				}
				
				List<SingleVariableDeclaration> parameters = method.parameters();
				for (SingleVariableDeclaration param : parameters) {
					if(param.resolveBinding() != null){
						methodInfo.getArguments().add(new AttributeVO(param.resolveBinding().getType().getName(), param.getName().toString(), null));
					}
				}
				
				
  				classInfo.getMethods().add(methodInfo);
			}
  			
  			
  			
  		}
  		return classInfo;

	}
	
	public static List<String> getJarsInfolder(String binariesFolder){
		List<String> jars = new ArrayList<String>();
		String[] files = (new File(binariesFolder)).list();
		for (String file : files) {
			if(file.endsWith(".jar")){
				jars.add(file);
			}
		}
		return jars;
	}
	
	
	public static String getSignature(String methodName, List<StatementLocation> calls){
		StringBuffer signature = new StringBuffer();
		signature.append(methodName);
		signature.append(FIELD_SEPARATOR);
		for (StatementLocation statementLocation : calls) {
			signature.append(statementLocation.getExpression());
			signature.append(METHOD_SEPARATOR_IN_PATTERN);
			
		}
		
		return signature.toString();
	}
}
