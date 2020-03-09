package se.eyevinn.listenup.processor.model;

import java.util.List;

import com.mattiasselin.linewriter.ILineSource;
import com.mattiasselin.linewriter.LineWriter;

import se.eyevinn.listenup.processor.util.IImportContext;
import se.eyevinn.listenup.processor.util.ITypeReference;
import se.eyevinn.listenup.processor.util.LocalVariableNamePicker;
import se.eyevinn.listenup.processor.util.StringUtil;
import se.eyevinn.listenup.processor.util.StringUtil.IStringJoiner;

/**
 * 
 * @author Mattias Selin
 *
 */
public class AbstractMethodModel {
	public final ITypeReference returnType;
	public final String name;
	public final ParameterModel[] parameters;
	
	public AbstractMethodModel(ITypeReference returnType, String name, List<ParameterModel> parameterModels) {
		this(returnType, name, parameterModels.toArray(new ParameterModel[parameterModels.size()]));
	}
	
	public AbstractMethodModel(ITypeReference returnType, String name, ParameterModel ... parameters) {
		this.returnType = returnType;
		this.name = name;
		this.parameters = parameters;
	}
	
	public ILineSource override(IImportContext importContext, ILineSource methodBody) {
		LineWriter lineWriter = new LineWriter();
		IStringJoiner parametersJoin = StringUtil.joiner(", ");
		for(ParameterModel parameter : parameters) {
			parametersJoin.add(importContext.getName(parameter.type)+" "+parameter.name);
		}
		lineWriter.println("@Override");
		lineWriter.println("public "+importContext.getName(returnType)+" "+name+"("+parametersJoin+") {");
			lineWriter.indent();
			methodBody.writeTo(lineWriter);
			lineWriter.unindent();
		lineWriter.println("}");
		return lineWriter;
	}
	

	public void registerParameterNames(LocalVariableNamePicker namePicker) {
		for(ParameterModel parameter : parameters) {
			namePicker.supplyUsedName(parameter.name);
		}
	}
}
