<h1>Delegation service</h1>
<td id="wikicontent" class="psdescription">
  <p>
    Delegation need to be handled with care. 
  </p>
  <p>
    The person delegation his/her work has to:  
  </p>
  <ul>
    <li>
      validate his/her identity by digital signing the delegation.  
    </li>
    <li>
      specify what type of work should be delegated.  
    </li>
    <li>
      specify who (other user or group of users) is authorized to do the work. 
    </li>
  </ul>
  <p>
  </p>
  <p>
    The delegation service will also facilitate revisions/audits of active delegations. Partikular care has to be taken when the person delegating tasks change work responsibilities or leave his/her position. 
  </p>
  <p>
    More about project from icc :
    <a href="http://icc.vgregion.se/index.php/Delegeringstj%C3%A4nsten_-_RGF" rel="nofollow">
      http://icc.vgregion.se/index.php/Delegeringstj%C3%A4nsten_-_RGF
    </a>
     (swedish) 
  </p>
  <p>
    More about the solution: 
    <a href="https://github.com/Vastra-Gotalandsregionen/oppna-program-delegationservice/wiki/TheSolution" rel="nofollow">
      https://github.com/Vastra-Gotalandsregionen/oppna-program-delegationservice/wiki/TheSolution
    </a>
     (swedish) 
  </p>
  <p>
    <img src="https://raw.githubusercontent.com/wiki/Vastra-Gotalandsregionen/oppna-program-delegationservice/images/DelegationServiceOverview.png"/>
  </p>
  <p>
    Below are sequence diagrams that describes two operations of the application. The other operations follow these examples also.  
  </p>
  <p>
    <img src="https://raw.githubusercontent.com/wiki/Vastra-Gotalandsregionen/oppna-program-delegationservice/images/DelegationServiceSeqGetDelegation.png"/>
  </p>
  <p>
    <img src="https://raw.githubusercontent.com/wiki/Vastra-Gotalandsregionen/oppna-program-delegationservice/images/DelegationServiceSeqSave.png"/>
  </p>
  <p>
    <a href="https://code.google.com/p/oppna-program-delegationservice/wiki/TheSolution" rel="nofollow">
      Read more ...
    </a>
  </p>
</td>

<h2>Bygganvisningar</h2>

To build the project, two other components must available in the maven-library: oppna-program-icc
and monitoring-schemas.

These are under git repo: https://github.com/Vastra-Gotalandsregionen/oppna-program-icc.
Under this path, run a maven build / install:
Bygg oppna-program-icc\service-descriptions\authorization-delegation-schemas\tags\authorization-delegation-schemas-1.0.7\

And, also, build: https://github.com/Vastra-Gotalandsregionen/monitoring-schemas

<h2>oppna-program-delegationservice</h2>
 
Is part of Västra Götaland's commitment to open source under the
<a href="https://github.com/Vastra-Gotalandsregionen//oppna-program">
  Öppna Program
</a> project.
