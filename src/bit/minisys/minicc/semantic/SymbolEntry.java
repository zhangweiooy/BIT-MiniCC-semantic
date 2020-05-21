package bit.minisys.minicc.semantic;

enum SymbolKind{
	SYM_FUNC,
	SYM_VAR
}

public class SymbolEntry {
	public String name;
	public String type;
	public SymbolKind kind;
	public int tokenId;
	
}
