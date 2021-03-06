package bit.minisys.minicc.semantic;

import java.util.ArrayList;

public class SymbolTable {
	private ArrayList<SymbolEntry> symbolEntries;
	
	public SymbolTable() {
		this.setSymbolEntries(new ArrayList<SymbolEntry>());
	}
	
	public void insertSybolEnty(SymbolEntry symbolEntry) {
		this.symbolEntries.add(symbolEntry);
	}
	
	public SymbolEntry getSymbolEntryByName(String name) {
		SymbolEntry symbolEntry = null;
		for (SymbolEntry se : this.symbolEntries) {
			if (se.name.equals(name)) {
				symbolEntry = se;
				break;
			}
		}
		return symbolEntry;
	}
	
	public ArrayList<SymbolEntry> getSymbolEntries() {
		return this.symbolEntries;
	}
	
	public void setSymbolEntries(ArrayList<SymbolEntry> symbolEntries) {
		this.symbolEntries = symbolEntries;
	}
}
