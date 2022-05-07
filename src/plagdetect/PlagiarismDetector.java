package plagdetect;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class PlagiarismDetector implements IPlagiarismDetector {
	
	private int n; //length of ngrams
	private Map<String, Map<String, Integer>> compareFiles = new HashMap<>();
	private Map<String, Set<String>> files = new HashMap<>();
	
	public PlagiarismDetector(int n) {
		
		this.n= n;
	}
	
	@Override
	public int getN() {
		
		return this.n;
	}

	@Override
	public Collection<String> getFilenames() {
		
		return files.keySet();
	}

	@Override
	public Collection<String> getNgramsInFile(String filename) {

		return files.get(filename);
	}

	@Override
	public int getNumNgramsInFile(String filename) {

		return files.get(filename).size();
	}

	@Override
	public Map<String, Map<String, Integer>> getResults() {
		
		return compareFiles;
	}

	@Override
	public void readFile(File file) throws IOException {
		//Auto-generated method stub
		// most of your work can happen in this method
		
		//Gets ngrams for the given file;
		Scanner scan = new Scanner(file);
		Set<String> ngrams = new HashSet<>();
		
		while(scan.hasNextLine()) {
			String s = scan.nextLine();
			String[] words = s.split(" ");
			for(int i=0; i<=words.length-n; i++) {
				String temp = "";
				for (int j=0; j<n; j++) {
					temp = temp + words[i+j]+" ";
				}
				temp = temp.substring(0,temp.length()-1);
				ngrams.add(temp);
			}
		}
		scan.close();
		
		//Gets comparisons between all read-in files
		Iterator<String> it = files.keySet().iterator();
		Map<String,Integer> tempMap = new HashMap<>();
		
		while(it.hasNext()) {
			int count = 0;
			String key = it.next();
			Iterator<String> jt = files.get(key).iterator();
			while(jt.hasNext()) {
				if(ngrams.contains(jt.next())) {
					count += 1;
				}
			}
			
			tempMap.put(key, count);
			
		}
		
		//Stores data in instance variables
		compareFiles.put(file.getName(), tempMap);
		files.put(file.getName(), ngrams);
		
		
	}

	@Override
	public int getNumNGramsInCommon(String file1, String file2) {
		// TODO Auto-generated method stub
		if(compareFiles.get(file1).containsKey(file2)) {
			return compareFiles.get(file1).get(file2);
		}
		return compareFiles.get(file2).get(file1);
	}

	@Override
	public Collection<String> getSuspiciousPairs(int minNgrams) {
		// TODO Auto-generated method stub
		Set<String> results = new HashSet<>();
		Iterator<String> it = compareFiles.keySet().iterator();
		while(it.hasNext()) {
			String file1 = it.next();
			Iterator<String> jt = compareFiles.get(file1).keySet().iterator();
			while(jt.hasNext()) {
				String file2= jt.next();
				int x = compareFiles.get(file1).get(file2);
				if (x>=minNgrams) {
					if(file1.compareTo(file2)<0) {
						results.add(file1 +" "+ file2 +" "+ x);
					}else {
						results.add(file2 +" "+ file1 +" "+ x);
					}
				}
			}
		}
		return results;
	}

	@Override
	public void readFilesInDirectory(File dir) throws IOException {
		// delegation!
		// just go through each file in the directory, and delegate
		// to the method for reading a file
		for (File f : dir.listFiles()) {
			readFile(f);
		}
	}
}
