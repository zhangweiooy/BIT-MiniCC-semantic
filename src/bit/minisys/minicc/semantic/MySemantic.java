package bit.minisys.minicc.semantic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import bit.minisys.minicc.parser.ast.ASTArrayAccess;
import bit.minisys.minicc.parser.ast.ASTArrayDeclarator;
import bit.minisys.minicc.parser.ast.ASTBinaryExpression;
import bit.minisys.minicc.parser.ast.ASTBreakStatement;
import bit.minisys.minicc.parser.ast.ASTCastExpression;
import bit.minisys.minicc.parser.ast.ASTCharConstant;
import bit.minisys.minicc.parser.ast.ASTCompilationUnit;
import bit.minisys.minicc.parser.ast.ASTCompoundStatement;
import bit.minisys.minicc.parser.ast.ASTConditionExpression;
import bit.minisys.minicc.parser.ast.ASTContinueStatement;
import bit.minisys.minicc.parser.ast.ASTDeclaration;
import bit.minisys.minicc.parser.ast.ASTDeclarator;
import bit.minisys.minicc.parser.ast.ASTExpression;
import bit.minisys.minicc.parser.ast.ASTExpressionStatement;
import bit.minisys.minicc.parser.ast.ASTFloatConstant;
import bit.minisys.minicc.parser.ast.ASTFunctionCall;
import bit.minisys.minicc.parser.ast.ASTFunctionDeclarator;
import bit.minisys.minicc.parser.ast.ASTFunctionDefine;
import bit.minisys.minicc.parser.ast.ASTGotoStatement;
import bit.minisys.minicc.parser.ast.ASTIdentifier;
import bit.minisys.minicc.parser.ast.ASTInitList;
import bit.minisys.minicc.parser.ast.ASTIntegerConstant;
import bit.minisys.minicc.parser.ast.ASTIterationDeclaredStatement;
import bit.minisys.minicc.parser.ast.ASTIterationStatement;
import bit.minisys.minicc.parser.ast.ASTLabeledStatement;
import bit.minisys.minicc.parser.ast.ASTMemberAccess;
import bit.minisys.minicc.parser.ast.ASTNode;
import bit.minisys.minicc.parser.ast.ASTParamsDeclarator;
import bit.minisys.minicc.parser.ast.ASTPostfixExpression;
import bit.minisys.minicc.parser.ast.ASTReturnStatement;
import bit.minisys.minicc.parser.ast.ASTSelectionStatement;
import bit.minisys.minicc.parser.ast.ASTStatement;
import bit.minisys.minicc.parser.ast.ASTStringConstant;
import bit.minisys.minicc.parser.ast.ASTToken;
import bit.minisys.minicc.parser.ast.ASTTypename;
import bit.minisys.minicc.parser.ast.ASTUnaryExpression;
import bit.minisys.minicc.parser.ast.ASTUnaryTypename;
import bit.minisys.minicc.parser.ast.ASTVariableDeclarator;
import bit.minisys.minicc.parser.ast.ASTVisitor;

public class MySemantic implements IMiniCCSemantic,ASTVisitor{
	private SymbolTable symbolTable = new SymbolTable();
	private HashMap<String, SymbolTable> funcSymbolTable= new HashMap<String,SymbolTable>();
	String funcName = null;
	private ArrayList<String> errors = new ArrayList<String>();
	private int iterationFlag = 0;
	private boolean breakFlag = false;
	
	@Override
	public String run(String iFile) throws Exception {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		ASTCompilationUnit program = mapper.readValue(new File(iFile), ASTCompilationUnit.class);
		System.out.println("Semanticing...");
		
		visit(program);
		
		for (int i = 0; i < errors.size(); i++) {
			System.out.println("ES"+String.format("%02d", i)+"  "+i+errors.get(i));
		}
		return null;
	}
	@Override
	public void visit(ASTCompilationUnit program) throws Exception {
		// TODO Auto-generated method stub
		for (ASTNode iterable : program.items) {
			if(iterable instanceof ASTDeclaration) {
				visit((ASTDeclaration)iterable);
			}else if(iterable instanceof ASTFunctionDefine) {
				visit((ASTFunctionDefine)iterable);
			}
		}
	}
	@Override
	public void visit(ASTDeclaration declaration) throws Exception {
		// TODO Auto-generated method stub

		for (ASTInitList  iterable : declaration.initLists) {
			SymbolEntry se = new SymbolEntry();
			se.name = ((ASTVariableDeclarator)iterable.declarator).identifier.value;
			se.kind = SymbolKind.SYM_VAR;
			se.tokenId = ((ASTVariableDeclarator)iterable.declarator).identifier.tokenId;
			se.type = declaration.specifiers.get(declaration.specifiers.size()-1).value;
			if (this.funcName == null) {
				if(this.symbolTable.getSymbolEntryByName(se.name) != null){
					errors.add("_var_defined_again.c"+":"+se.tokenId+"\t"+se.name);
				}else{
					this.symbolTable.insertSybolEnty(se);
				}
			}else {
				SymbolTable st = funcSymbolTable.get(funcName);
				if(st.getSymbolEntryByName(se.name) != null){
					errors.add("_var_defined_again.c"+":"+se.tokenId+"\t"+se.name);
				}else{
					st.insertSybolEnty(se);
					this.funcSymbolTable.put(funcName, st);
				}
			}
		}
	}
	@Override
	public void visit(ASTArrayDeclarator arrayDeclarator) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTVariableDeclarator variableDeclarator) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTFunctionDeclarator functionDeclarator) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTParamsDeclarator paramsDeclarator) throws Exception {
		// TODO Auto-generated method stub
		SymbolTable st;
		st = funcSymbolTable.get(funcName);
		SymbolEntry sEntry = new SymbolEntry();
		sEntry.name = ((ASTVariableDeclarator)paramsDeclarator.declarator).identifier.value;
		sEntry.kind = SymbolKind.SYM_VAR;
		sEntry.tokenId = ((ASTVariableDeclarator)paramsDeclarator.declarator).identifier.tokenId;
		sEntry.type = paramsDeclarator.specfiers.get(paramsDeclarator.specfiers.size()-1).value;
		
		if(st.getSymbolEntryByName(sEntry.name) != null){
			errors.add("_var_defined_again.c"+":"+sEntry.tokenId+"\t"+sEntry.name);
		}else{
			st.insertSybolEnty(sEntry);
			funcSymbolTable.put(funcName, st);
		}
	}
	@Override
	public void visit(ASTArrayAccess arrayAccess) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTBinaryExpression binaryExpression) throws Exception {
		// TODO Auto-generated method stub
		visit((ASTExpression)binaryExpression.expr1);
		visit((ASTExpression)binaryExpression.expr2);
	}
	@Override
	public void visit(ASTBreakStatement breakStat) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTContinueStatement continueStatement) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTCastExpression castExpression) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTCharConstant charConst) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTCompoundStatement compoundStat) throws Exception {
		// TODO Auto-generated method stub
		for (ASTNode iterable : compoundStat.blockItems) {
			if(iterable instanceof ASTDeclaration) {
				visit((ASTDeclaration)iterable);
			}else if(iterable instanceof ASTSelectionStatement) {
				visit((ASTSelectionStatement)iterable);
			}else if (iterable instanceof ASTIterationStatement) {
				this.iterationFlag++;
				visit((ASTIterationStatement)iterable);
				if (this.iterationFlag > 0  ) {
					if (this.breakFlag == false) {
						this.iterationFlag--;
					}else {
						
					}
				}else {
					if (this.breakFlag == true) {
						
					}else {
						this.errors.add("_break_not_in_loop.c");
					}
				}
			}else if (iterable instanceof ASTExpressionStatement) {
				visit((ASTExpressionStatement)iterable);
			}else if (iterable instanceof ASTReturnStatement) {
				visit((ASTReturnStatement)iterable);
			}else if (iterable instanceof ASTBreakStatement) {
				if (this.iterationFlag > 0) {
					this.iterationFlag--;
					this.breakFlag = true;
				}
				else {
					errors.add("_break_not_in_loop.c");
				}
			}
		}
	}
	@Override
	public void visit(ASTConditionExpression conditionExpression) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTExpression expression) throws Exception {
		// TODO Auto-generated method stub
		if (expression instanceof ASTIdentifier) {
			visit((ASTIdentifier)expression);
		}else if (expression instanceof ASTBinaryExpression) {
			visit((ASTBinaryExpression)expression);
		}else if (expression instanceof ASTPostfixExpression) {
			visit((ASTPostfixExpression)expression);
		}else if (expression instanceof ASTFunctionCall) {
			visit((ASTFunctionCall)expression);
		}else if (expression instanceof ASTIntegerConstant) {
			
		}else if (expression instanceof ASTStringConstant) {
			
		}else if (expression instanceof ASTCharConstant) {
			
		}else if (expression instanceof ASTFloatConstant) {
			
		}else if (expression instanceof ASTArrayAccess) {
			
		}
	}
	@Override
	public void visit(ASTExpressionStatement expressionStat) throws Exception {
		// TODO Auto-generated method stub
		for (ASTExpression iterable : expressionStat.exprs) {
			visit(iterable);
		}
	}
	@Override
	public void visit(ASTFloatConstant floatConst) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTFunctionCall funcCall) throws Exception {
		// TODO Auto-generated method stub
		if (this.symbolTable.getSymbolEntryByName(((ASTIdentifier)funcCall.funcname).value) == null) {
			errors.add("_var_not_defined.c"+":"+((ASTIdentifier)funcCall.funcname).tokenId.toString()+"\t"+((ASTIdentifier)funcCall.funcname).value);
		};
		for (ASTExpression iterable : funcCall.argList) {
			visit(iterable);
		}
	}
	@Override
	public void visit(ASTGotoStatement gotoStat) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTIdentifier identifier) throws Exception {
		// TODO Auto-generated method stub
		SymbolTable st;
		if (funcName == null) {
			st = this.symbolTable;
		}else {
			st = funcSymbolTable.get(funcName);
		}
		if (st.getSymbolEntryByName(identifier.value) == null) {
			errors.add("_var_not_defined.c"+":"+identifier.tokenId+"\t"+identifier.value);
		}
		
	}
	@Override
	public void visit(ASTInitList initList) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTIntegerConstant intConst) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTIterationDeclaredStatement iterationDeclaredStat) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTIterationStatement iterationStat) throws Exception {
		// TODO Auto-generated method stub
		for (ASTExpression iterable : iterationStat.init) {
			visit(iterable);
		}
		for (ASTExpression iterable : iterationStat.cond) {
			visit(iterable);
		}
		for (ASTExpression iterable : iterationStat.step) {
			visit(iterable);
		}
		visit((ASTCompoundStatement)iterationStat.stat);
	}
	@Override
	public void visit(ASTLabeledStatement labeledStat) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTMemberAccess memberAccess) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTPostfixExpression postfixExpression) throws Exception {
		// TODO Auto-generated method stub
		visit((ASTExpression)postfixExpression.expr);
	}
	@Override
	public void visit(ASTReturnStatement returnStat) throws Exception {
		// TODO Auto-generated method stub
		for (ASTExpression iterable : returnStat.expr) {
			visit(iterable);
		}
	}
	@Override
	public void visit(ASTSelectionStatement selectionStat) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTStringConstant stringConst) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTTypename typename) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTUnaryExpression unaryExpression) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTUnaryTypename unaryTypename) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTFunctionDefine functionDefine) throws Exception {
		// TODO Auto-generated method stub
		SymbolEntry symbolEntry = new SymbolEntry();
		this.funcName = ((ASTVariableDeclarator)((ASTFunctionDeclarator)functionDefine.declarator).declarator).identifier.value;
		symbolEntry.name = funcName;
		symbolEntry.kind = SymbolKind.SYM_FUNC;
		symbolEntry.tokenId = ((ASTVariableDeclarator)((ASTFunctionDeclarator)functionDefine.declarator).declarator).identifier.tokenId;
		symbolEntry.type = functionDefine.specifiers.get(functionDefine.specifiers.size()-1).value;
		if(this.symbolTable.getSymbolEntryByName(symbolEntry.name) != null){
			errors.add("_var_defined_again.c"+":"+symbolEntry.tokenId+"\t"+symbolEntry.name);
		}else{
			this.symbolTable.insertSybolEnty(symbolEntry);
		}
		
		SymbolTable st = new SymbolTable();
		this.funcSymbolTable.put(this.funcName, st);//为函数创建符号表
		for (ASTParamsDeclarator e : (((ASTFunctionDeclarator)functionDefine.declarator).params)) {//遍历函数的参数
			visit(e);
		}
		
		visit(functionDefine.body);//函数体
		this.funcName = null;
	}
	@Override
	public void visit(ASTDeclarator declarator) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTStatement statement) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(ASTToken token) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
