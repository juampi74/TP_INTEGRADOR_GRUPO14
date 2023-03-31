package org.argentinaprograma.TP_Integrador_Grupo14;

import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import org.apache.commons.lang3.StringUtils;

import modelo.Equipo;
import modelo.Participante;
import modelo.Partido;
import modelo.Pronostico;
import modelo.ResultadoEnum;
import modelo.Ronda;
import exceptions.NingunoOMasDeUnEquipoPronosticadoException;
import exceptions.NoEsNumeroEnteroException;
import exceptions.NumeroIncorrectoDeCamposException;
import servicios.LectorArchivos;

public class Main {
    
	public static void main(String[] args) {
		
        if(args.length != 2){
            System.out.println("ERROR: No ingresaste los archivos correspondientes!");
            System.exit(88);
        }
        
        try {
        	
        	//Valido que todo esté bien en los Archivos CSV
			boolean correcto = validarArchivos(args[0], args[1]);
			
			if(correcto) {
	        	
				LectorArchivos lectorArchivos1 = new LectorArchivos(args[0]);
		        
		        //Obtengo todas las líneas del archivo CSV "Resultados"
		        lectorArchivos1.parsearArchivo("R");
		        
		        //Creo equipos
		        ArrayList<Equipo> equipos = lectorArchivos1.crearEquipos();
		        
		        //Creo Partidos            
		        ArrayList<Partido> partidos = lectorArchivos1.crearPartidos(equipos);  
		        
		        //Creo Rondas
		        ArrayList<Ronda> rondas = lectorArchivos1.crearRondas(partidos);
		    
		        
		        LectorArchivos lectorArchivos2 = new LectorArchivos(args[1]);
		        
		        //Obtengo todas las líneas del archivo CSV "Pronosticos"
		        lectorArchivos2.parsearArchivo("P");
		        
		        //Creo Pronosticos           
		        ArrayList<Pronostico> pronosticos = lectorArchivos2.crearPronosticos(equipos, partidos);    

		        //Creo Participantes
		        ArrayList<Participante> participantes = lectorArchivos2.crearParticipantes(pronosticos);     
		        
		        //Muestro el Puntaje
		        mostrarPuntaje(rondas, pronosticos, participantes);
		        
				
	        }
	        		
		} catch (CsvValidationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumeroIncorrectoDeCamposException e) {
			System.out.println("\n ----- La cantidad de campos de los archivos ingresados es incorrecta! ----- ");
		} catch (NoEsNumeroEnteroException e) {
			System.out.println("\n ----- La Ronda y la Cantidad de Goles deben ser Numeros Enteros! ----- ");
		} catch (NingunoOMasDeUnEquipoPronosticadoException e) {
			System.out.println("\n ----- Se debe seleccionar un Equipo por Pronostico (Con una X)! ----- ");
		}
     
	}

	private static void mostrarPuntaje(ArrayList<Ronda> rondas, ArrayList<Pronostico> pronosticos, ArrayList<Participante> participantes) {
		
		Scanner scn = new Scanner(System.in);
		
		System.out.println("\n¿Quiere ver el Puntaje Total del Pronostico o el de una Ronda en Especifico? <T - R>");
        String mostrar = scn.nextLine();
        while(mostrar.compareToIgnoreCase("T") != 0 && mostrar.compareToIgnoreCase("R") != 0) {
        	System.out.println("Error! <T - R>");
        	mostrar = scn.nextLine();
        }
        
        if(mostrar.equalsIgnoreCase("T")) {
        	calcularPuntajeTotal(pronosticos, participantes);
        } else {
        	System.out.print("\nRondas Posibles: | ");
        	for(Ronda ronda : rondas) {
	        	System.out.print(ronda.getNro() + " | ");
	        }
        	
        	System.out.println("\nIngrese el Numero de una de las Rondas listadas: ");
        	String ronda = scn.nextLine();
        	
        	
        	while(!(StringUtils.isNumeric(ronda)) || Integer.parseInt(ronda) > (rondas.size()) || Integer.parseInt(ronda) <= 0) {
        		System.out.println("Error! Ronda Incorrecta:");
	        	ronda = scn.nextLine();
        	}
        	
        	int i = 0;
        	while(Integer.parseInt(ronda) != rondas.get(i).getNro()) {
        		i++;
        	}
        	
        	
        	calcularPuntajePorRonda((i + 1), pronosticos, participantes);
        }
		
	}

	public static void calcularPuntajeTotal(ArrayList<Pronostico> pronosticos, ArrayList<Participante> participantes) {
		
		int punt = 0;
			
		for(int i = 0; i < pronosticos.size(); i++) {
							
			punt = pronosticos.get(i).puntos();
				
			for(Participante participante : participantes) {
					
				if(participante.getNombre().equals(pronosticos.get(i).getParticipante())) {
					participante.setPuntajeTotal(participante.getPuntajeTotal() + punt);
				}
					
			}
				
		}
		
		System.out.println("\n");
		
		System.out.println("----------------------------");
		System.out.println("      Puntaje Total");
		System.out.println("----------------------------");
			
		participantes = ordenarParticipantes(participantes);
		
		for(Participante participante : participantes) {
			System.out.println(" - " + participante.getNombre() + ": " + participante.getPuntajeTotal());
		}
		
		System.out.println("----------------------------");
		System.out.println("\n");
		
		
	}
	
	public static void calcularPuntajePorRonda(int nroRonda, ArrayList<Pronostico> pronosticos, ArrayList<Participante> participantes) {
		
		int punt = 0;
			
		for(int i = 0; i < pronosticos.size(); i++) {
							
			if(pronosticos.get(i).getPartido().getRonda() == nroRonda) {
				
				punt = pronosticos.get(i).puntos();
				
				for(Participante participante : participantes) {
						
					if(participante.getNombre().equals(pronosticos.get(i).getParticipante())) {
						participante.setPuntajeTotal(participante.getPuntajeTotal() + punt);
					}
						
				}
				
			}
			
		}
		
		System.out.println("\n");
		
		System.out.println("----------------------------");
		System.out.println("   Puntajes en la Ronda " + nroRonda);
		System.out.println("----------------------------");
			
		participantes = ordenarParticipantes(participantes);
		
		for(Participante participante : participantes) {
			System.out.println(" - " + participante.getNombre() + ": " + participante.getPuntajeTotal());
		}
		
		System.out.println("----------------------------");
		System.out.println("\n");
		
	}

	public static ArrayList<Participante> ordenarParticipantes(ArrayList<Participante> participantes) {
		
		Collections.sort(participantes, Collections.reverseOrder());
		
		return participantes;
	}
	

	public static boolean validarArchivos(String rutaArchivoResultados, String rutaArchivoPronosticos) throws CsvValidationException, IOException, NumeroIncorrectoDeCamposException, NoEsNumeroEnteroException, NingunoOMasDeUnEquipoPronosticadoException {
		
		CSVReader lector1 = new CSVReader(new FileReader(rutaArchivoResultados));			
		String[] resultCSV;
		String[] resultados;
		int i = 0;
		
		while((resultCSV = lector1.readNext()) != null) {
			resultados = resultCSV[0].split(";");
			
			if(resultados.length != 5) {
				throw new NumeroIncorrectoDeCamposException();
			}
			
			if(i > 0) {
				
				if(!(StringUtils.isNumeric(resultados[0])) || !(StringUtils.isNumeric(resultados[2])) || !(StringUtils.isNumeric(resultados[3]))) {
					throw new NoEsNumeroEnteroException();
				}
					
			}
				
			i++;
			
		}
		
		CSVReader lector2 = new CSVReader(new FileReader(rutaArchivoPronosticos));			
		String[] pronostCSV;
		String[] pronosticos;
		int j = 0;
		
		while((pronostCSV = lector2.readNext()) != null) {
			pronosticos = pronostCSV[0].split(";");
			
			if(pronosticos.length != 6) {
				throw new NumeroIncorrectoDeCamposException();
			}
			
			if(j > 0) {
			
				int contCruces = 0;
				int contLlenas = 0;
				

				for(int k = 2; k < 5; k++) {
					
					if(pronosticos[k].equalsIgnoreCase("X")) {
						contCruces++;
					} 
					
					if(!(pronosticos[k].equals(""))) {
						contLlenas++;
					}
				}
				
				if(contCruces != 1 || contLlenas != 1) {
					throw new NingunoOMasDeUnEquipoPronosticadoException();
				}
				
			}
			
			j++;
			
		}
		
		return true;
	
	}
	
}