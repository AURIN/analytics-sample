############ SUPPORT METHODS ############
## Library loading methods
libraryError <- function() {
  
  errorLibraries <- FALSE
  if(!require("RJSONIO")) {
    errorLibraries <- TRUE
  }
  
  ## call the library version checker
  errorFin <- errorLibraries & libraryVersionError()
  return(errorFin)
}

## library version check
libraryVersionError <- function() {
  errorLibVersion <- FALSE
  
  if(compareVersion(toString(packageVersion("RJSONIO")), "1.0") == -1) {
    errorLibVersion <- TRUE
  }
  
  return(errorLibVersion)
}

## Check input data, formula string and family options validity
inputError <- function(dataFrameName, attributeName) {
  
  errorInput <- FALSE
  
  ## check input dataframe
  if(!(is.character(dataFrameName) && length(dataFrameName) >= 1)) {
    errorInput <- TRUE
  }
  
  if(!(is.character(attributeName) && length(attributeName) >= 1)) {
    errorInput <- TRUE
  }
  
  return(errorInput)
}

computeMean <- function() {
  
  oData <- "NULL"
  
  
  
  
  if(libraryError()) {
    # Libraries needed
    warning("Unable to load the required R-libraries", "\n")        
    return(oData)
  }
  
  if(!inputError(dataFrameName, attributeName)) {
    # do compute 
    
    # retrieve the dataF from the user supplied name,-dataFrameName- and pass to -dataF-. 
    dataF <- get(dataFrameName)
    # retrieve the attribute from the dataframe.
    attr3 <- dataF[attributeName]
    oData <- colMeans(attr3)
    #print(oData)
  }
  
  return(oData)
}

## RUNIT test case
TestcomputeMean <- function() {
  
  # setup inputs
  myData <<- data.frame(
    Col0=c(1.1, 2.2, 3.3, 11.1, 22.2, 33.3),
    Col1=c(10.0, 20.0, 30.0, 40.0, 50.0, 60.0), 
    Col2=c(100.0, 200.0, 300.0, 400.0, 500.0, 600.0),
    Col3=c(100.1, 200.2, 300.3, 110.1, 220.2, 3300.3)
  )
  
  print("assigning args")
  attributeName <<- "Col0"
  dataFrameName <<- "myData"
  
  # Check input for errors
  if(libraryError()) {
    # Libraries needed
    warning("Unable to load the required R-libraries", "\n")
    return(oData)
  }
  if(!inputError(dataFrameName, attributeName)) {
    
    # Call the main method
    computeMean()
  }
}

## TEST RUN
#TestcomputeMean()

## main method call
computeMean()
