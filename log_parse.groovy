def log_parse(){
	node('master'){
		stage('log_parse'){
			sleep 10
			logParser projectRulePath: 'log_parse', showGraphs: true, useProjectRule: true		}
	}
}

def log_parse_1(){
	node('master'){
		stage('log_parse'){
			sleep 10
			logParser projectRulePath: 'log_parse', showGraphs: true, useProjectRule: true		}
	}
}

return this