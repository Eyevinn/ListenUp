package se.eyevinn.listenup.processor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 
 * @author Mattias Selin
 *
 */
public class ReferenceFactory {
	public ITypeReference c(Class<?> clazz) {
		return new ClassReference(clazz);
	}
	
	public ITypeReference c(String qname) {
		return new ClassReference(qname);
	}
	
	public ITypeReference g(ITypeReference genericType, ITypeReference ... typeParameters) {
		return new GenericTypeReference(genericType, typeParameters);
	}
	
	public ITypeReference g(ITypeReference genericType, List<ITypeReference> typeParameters) {
		return g(genericType, typeParameters.toArray(new ITypeReference[typeParameters.size()]));
	}
	
	public ITypeReference g(Class<?> genericType, ITypeReference ... typeParameters) {
		return g(c(genericType), typeParameters);
	}
	
	public ITypeReference g(String genericType, ITypeReference ... typeParameters) {
		return g(c(genericType), typeParameters);
	}
	
	public ITypeReference _boolean() {
		return PrimitiveReference.BOOLEAN;
	}
	
	public ITypeReference _byte() {
		return PrimitiveReference.BYTE;
	}
	
	public ITypeReference _char() {
		return PrimitiveReference.CHAR;
	}
	
	public ITypeReference _double() {
		return PrimitiveReference.DOUBLE;
	}
	
	public ITypeReference _float() {
		return PrimitiveReference.FLOAT;
	}
	
	public ITypeReference _int() {
		return PrimitiveReference.INT;
	}
	
	public ITypeReference _long() {
		return PrimitiveReference.LONG;
	}
	
	public ITypeReference _short() {
		return PrimitiveReference.SHORT;
	}
	
	public ITypeReference parse(String nestedQname) {
		TokenParser tokenParser = new TokenParser();
		tokenize(new StringStream(nestedQname), tokenParser);
		return tokenParser.getResult();
	}
	
	private class TokenParser implements ISink<IToken> {
		private boolean expectingType = true;
		private List<ITypeReference> typeReferences = new ArrayList<>();
		private Stack<List<ITypeReference>> stack = new Stack<>();

		@Override
		public void give(IToken token) {
			if(token instanceof QnameToken) {
				if(expectingType) {
					typeReferences.add(new ClassReference(((QnameToken) token).qname));
					expectingType = false;
				} else {
					throw new RuntimeException("Didn't expect type");
				}
			} else {
				char syntax = ((SyntaxToken) token).syntax;
				if(syntax == '<') {
					stack.push(typeReferences);
					typeReferences = new ArrayList<>();
					expectingType = true;
				} else if(syntax == ',') {
					if(!expectingType) {
						expectingType = true;
					} else {
						throw new RuntimeException("Expected type");
					}
				} else if(syntax == '>') {
					expectingType = false;
					List<ITypeReference> oldRefs = stack.pop();
					ITypeReference last = oldRefs.remove(oldRefs.size()-1);
					oldRefs.add(g(last, typeReferences));
					typeReferences = oldRefs;
				}
			}
		}

		public ITypeReference getResult() {
			//TODO check if result available first
			return typeReferences.get(typeReferences.size()-1);
		}
	}
	
	private static void tokenize(StringStream stream, ISink<IToken> tokenSink) {
		StringBuilder builder = new StringBuilder();
		while(stream.hasNext()) {
			char c = stream.next();
			if(c == '<' || c == '>' || c == ',') {
				if(builder.length() > 0) {
					tokenSink.give(new QnameToken(builder.toString()));
					builder.setLength(0);
				}
				tokenSink.give(new SyntaxToken(c));
			} else {
				builder.append(c);
			}
		}
		if(builder.length() > 0) {
			tokenSink.give(new QnameToken(builder.toString()));
			builder.setLength(0);
		}
	}
	
	private interface IToken {
	}
	
	private static class QnameToken implements IToken {
		public final String qname;

		public QnameToken(String qname) {
			this.qname = qname;
		}
	}
	
	private static class SyntaxToken implements IToken {
		public final char syntax;

		public SyntaxToken(char syntax) {
			this.syntax = syntax;
		}
	}
	private static class StringStream {
		private final String string;
		private int index = -1;
		
		public StringStream(String string) {
			this.string = string;
		}
		
		public char next() {
			return string.charAt(++index);
		}
		
		public boolean hasNext() {
			return index+1 < string.length();
		}
	}
	
	private interface ISink<T> {
		void give(T value);
	}
}